package com.practice.drm.transfer.service;

import com.practice.drm.clients.customer.AccountDto;
import com.practice.drm.clients.customer.CustomerClient;
import com.practice.drm.clients.customer.CustomerDto;
import com.practice.drm.clients.fraud.FraudClient;
import com.practice.drm.clients.notification.NotificationClient;
import com.practice.drm.clients.notification.NotificationRequest;
import com.practice.drm.clients.transfer.TransferRequest;
import com.practice.drm.clients.transfer.TransferResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final CustomerClient customerClient;
    private final FraudClient fraudClient;
    private final NotificationClient notificationClient;

    public TransferResponse transfer(String login, TransferRequest req) {

        List<String> ownErrors = new ArrayList<>();
        List<String> otherErrors = new ArrayList<>();

        var sender = customerClient.getCustomer(login);

        if (fraudClient.isFraudster(sender.id()).isFraudster()) {
            ownErrors.add("Fraud detected");
            return TransferResponse.errors(ownErrors);
        }

        var receiver = login.equals(req.toLogin())
                ? sender
                : customerClient.getCustomer(req.toLogin());

        var fromAcc = find(sender, req.fromCurrency());
        var toAcc = find(receiver, req.toCurrency());

        if (fromAcc.isEmpty()) {
            ownErrors.add("Source account not found");
        }
        if (toAcc.isEmpty()) {
            (login.equals(req.toLogin()) ? ownErrors : otherErrors)
                    .add("Destination account not found");
        }
        if (!ownErrors.isEmpty() || !otherErrors.isEmpty()) {
            return buildError(ownErrors, otherErrors);
        }

        var amount = req.value();
        if (fromAcc.get().balance().compareTo(amount) < 0) {
            ownErrors.add("Insufficient funds");
            return TransferResponse.errors(ownErrors);
        }

        /* Обновляем балансы */
        // todo: make it transactional
        customerClient.updateAccountBalance(
                login, req.fromCurrency(),
                fromAcc.get().balance().subtract(amount));
        customerClient.updateAccountBalance(
                receiver.login(), req.toCurrency(),
                toAcc.get().balance().add(amount));

        /* Notification */
        var msg = String.format(
                "Перевод %.2f %s со счёта %s на %s",
                amount, req.fromCurrency(), req.fromCurrency(), req.toCurrency());

        notificationClient.sendNotification(
                new NotificationRequest(sender.id(), sender.name(), msg)
        );
        if (!sender.login().equals(receiver.login())) {
            notificationClient.sendNotification(
                    new NotificationRequest(receiver.id(), receiver.name(), msg)
            );
        }

        return TransferResponse.success();
    }

    /* helpers */
    private static Optional<AccountDto> find(CustomerDto c, String code) {
        return c.accounts().stream()
                .filter(a -> a.currency().getCode().equals(code))
                .findFirst();
    }

    private static TransferResponse buildError(
            List<String> own, List<String> other) {

        if (!own.isEmpty() && !other.isEmpty()) {
            return TransferResponse.fullError(own, other);
        }
        if (!own.isEmpty()) {
            return TransferResponse.errors(own);
        }
        return TransferResponse.otherErrors(other);
    }
}
