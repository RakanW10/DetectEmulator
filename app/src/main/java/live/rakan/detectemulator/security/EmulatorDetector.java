package live.rakan.detectemulator.security;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import android.os.Build;
import android.util.Log;

public class EmulatorDetector {
    // -------------------------- check using IP Address --------------------------
    public static boolean checkIPAddress() {
        String[] knownEmulatorIpRanges = {
                "10.0.",   // Android Emulator
                "192.168.", // Genymotion
                // Add other emulator IP ranges as needed
        };


        String ipAddress = getLocalIpAddress();

        if (ipAddress != null) {
            for (String range : knownEmulatorIpRanges) {
                if (ipAddress.startsWith(range)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            if (networkInterfaces != null) {
                while (networkInterfaces.hasMoreElements()) {
                    NetworkInterface networkInterface = networkInterfaces.nextElement();
                    Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress address = addresses.nextElement();
                        if (!address.isLoopbackAddress() && address.getHostAddress() != null) {
                            return address.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }
    // -------------------------- end check IP Address --------------------------
    // ------------------------ check throw device props  -----------------------
    public static boolean isEmulator() {
        boolean result = Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("sdk_gphone64_arm64")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator");

        // Logging all checks using Log.d
        LogCheck("Brand and Device check", Build.BRAND,Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"));
        LogCheck("Fingerprint check (generic)", Build.FINGERPRINT, Build.FINGERPRINT.startsWith("generic"));
        LogCheck("Fingerprint check (unknown)", Build.BRAND, Build.FINGERPRINT.startsWith("unknown"));
        LogCheck("Hardware check (goldfish)", Build.HARDWARE, Build.HARDWARE.contains("goldfish"));
        LogCheck("Hardware check (ranchu)", Build.HARDWARE, Build.HARDWARE.contains("ranchu"));
        LogCheck("Model check (google_sdk)", Build.MODEL, Build.MODEL.contains("google_sdk"));
        LogCheck("Model check (Emulator)", Build.MODEL, Build.MODEL.contains("Emulator"));
        LogCheck("Model check (Android SDK built for x86)", Build.MODEL,Build.MODEL.contains("Android SDK built for x86"));
        LogCheck("Manufacturer check (Genymotion)", Build.MANUFACTURER, Build.MANUFACTURER.contains("Genymotion"));
        LogCheck("Product check (sdk_google)", Build.PRODUCT, Build.PRODUCT.contains("sdk_google"));
        LogCheck("Product check (google_sdk)", Build.PRODUCT, Build.PRODUCT.contains("google_sdk"));
        LogCheck("Product check (sdk)", Build.PRODUCT, Build.PRODUCT.contains("sdk"));
        LogCheck("Product check (sdk_x86)", Build.PRODUCT, Build.PRODUCT.contains("sdk_x86"));
        LogCheck("Product check (sdk_gphone64_arm64)", Build.PRODUCT, Build.PRODUCT.contains("sdk_gphone64_arm64"));
        LogCheck("Product check (vbox86p)", Build.PRODUCT, Build.PRODUCT.contains("vbox86p"));
        LogCheck("Product check (emulator)", Build.PRODUCT, Build.PRODUCT.contains("emulator"));
        LogCheck("Product check (simulator)",  Build.PRODUCT, Build.PRODUCT.contains("simulator"));

        // Log the final result
        Log.d("EmulatorCheck", "Final result: " + result);

        return result;

        // goldFish
        // https://android.googlesource.com/platform/external/qemu/+/emu-master-dev/android/docs/GOLDFISH-VIRTUAL-HARDWARE.TXT
        //
    }
    private static  void LogCheck(String checkName,String content, boolean checkResult) {
        Log.d("EmulatorCheck", checkName + " # "+ content+" # " + (checkResult ? "true" : "false"));
    }
    // ------------------------ end check throw device props  -----------------------


}

