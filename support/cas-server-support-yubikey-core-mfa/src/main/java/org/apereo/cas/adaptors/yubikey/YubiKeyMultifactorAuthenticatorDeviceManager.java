package org.apereo.cas.adaptors.yubikey;

import org.apereo.cas.authentication.device.MultifactorAuthenticationDeviceManager;
import org.apereo.cas.authentication.device.MultifactorAuthenticationRegisteredDevice;
import org.apereo.cas.authentication.principal.Principal;
import lombok.RequiredArgsConstructor;
import lombok.val;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is {@link YubiKeyMultifactorAuthenticatorDeviceManager}.
 *
 * @author Misagh Moayyed
 * @since 7.1.0
 */
@RequiredArgsConstructor
public class YubiKeyMultifactorAuthenticatorDeviceManager implements MultifactorAuthenticationDeviceManager {
    private final YubiKeyAccountRegistry yubiKeyAccountRegistry;

    @Override
    public List<MultifactorAuthenticationRegisteredDevice> findRegisteredDevices(final Principal principal) {
        val registrations = yubiKeyAccountRegistry.getAccount(principal.getId());
        return registrations
            .stream()
            .map(this::mapAccount)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    protected List<MultifactorAuthenticationRegisteredDevice> mapAccount(final YubiKeyAccount acct) {
        return acct
            .getDevices()
            .stream()
            .map(device -> MultifactorAuthenticationRegisteredDevice.builder()
                .id(String.valueOf(device.getId()))
                .name(device.getName())
                .payload(device.toJson())
                .lastUsedDateTime(device.getRegistrationDate().toString())
                .model(device.getPublicId())
                .source("YubiKey")
                .build())
            .collect(Collectors.toList());
    }
}
