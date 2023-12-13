package es.chiteroman.safetynetfix;

import android.os.Build;
import android.util.Log;

import java.lang.reflect.Field;
import java.security.Provider;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;

public final class EntryPoint {
    private static final Map<String, String> map = new HashMap<>();

    static {
        map.put("PRODUCT", "foster_e_hdd");
        map.put("DEVICE", "foster");
        map.put("MANUFACTURER", "NVIDIA");
        map.put("BRAND", "NVIDIA");
        map.put("MODEL", "SHIELD Android TV");
        map.put("FINGERPRINT", "NVIDIA/foster_e_hdd/foster:7.0/NRD90M/2427173_1038.2788:user/release-keys");
    }

    public static void init() {
        spoofDevice();

        try {
            Provider provider = Security.getProvider("AndroidKeyStore");

            Provider customProvider = new CustomProvider(provider);

            Security.removeProvider("AndroidKeyStore");
            Security.insertProviderAt(customProvider, 1);

            LOG("Spoof KeyStoreSpi and Provider done!");

        } catch (Exception e) {
            LOG("ERROR: " + e);
        }
    }

    static void spoofDevice() {
        map.forEach(EntryPoint::setProp);
    }

    private static void setProp(String name, String value) {
        try {
            Field field = Build.class.getDeclaredField(name);
            field.setAccessible(true);
            String oldValue = (String) field.get(null);
            field.set(null, value);
            field.setAccessible(false);
            if (value.equals(oldValue)) return;
            LOG(String.format("Field: '%s' with value '%s' is now set to '%s'", name, oldValue, value));
        } catch (Exception e) {
            LOG("setprop exception: " + e);
        }
    }

    static void LOG(String msg) {
        Log.d("SNFix/Java", msg);
    }
}