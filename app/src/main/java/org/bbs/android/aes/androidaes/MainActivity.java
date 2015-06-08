package org.bbs.android.aes.androidaes;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            new MyCipher().doit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // http://www.cnblogs.com/carlosk/archive/2012/05/18/2507975.html
    public static class MyCipher {

        private static final String UTF_8 = "utf-8";
        private static final String TAG = MyCipher.class.getSimpleName();

        protected String mBase64CipheredText;
        protected String mClearText;
        protected String mSeed;

        public MyCipher() {
            mSeed = "qwer3as2jin4fdsa";
            mClearText = "1234567890";
            mBase64CipheredText = "wF7sjX9ON7UTWBBtzpU2iA==";

        }

        public void doit() throws Exception {
            byte[] originalData = mClearText.getBytes(UTF_8);
            byte[] encryptedData = encrypt(getRawKey(mSeed.getBytes(UTF_8)), originalData);
            byte[] decryptedData = decrypt(getRawKey(mSeed.getBytes(UTF_8)), encryptedData);

            Log.d(TAG, "originalData: " + toHex(originalData));
            Log.d(TAG, "encryptedData: " + toHex(encryptedData));
            Log.d(TAG, "decryptedData: " + toHex(decryptedData));
        }

        private static byte[] encrypt(byte[] key, byte[] clear) throws Exception {
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
//        Cipher cipher = Cipher.getInstance("AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(
                    new byte[cipher.getBlockSize()]));
            byte[] encrypted = cipher.doFinal(clear);

            return encrypted;
        }

        private static byte[] decrypt(byte[] key, byte[] encrypted)
                throws Exception {
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
//        Cipher cipher = Cipher.getInstance("AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(
                    new byte[cipher.getBlockSize()]));
            byte[] decrypted = cipher.doFinal(encrypted);
            return decrypted;
        }

        private static byte[] getRawKey(byte[] seed) throws Exception {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom sr = null;
            //http://bbs.csdn.net/topics/390618237?page=1
            if (android.os.Build.VERSION.SDK_INT >=  17) {
                sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
            } else {
                sr = SecureRandom.getInstance("SHA1PRNG");
            }
            sr.setSeed(seed);
            kgen.init(128, sr);
            SecretKey sKey = kgen.generateKey();
            byte[] raw = sKey.getEncoded();

            return raw;
        }

        public static String toHex(String txt) {
            return toHex(txt.getBytes());
        }

        public static String fromHex(String hex) {
            return new String(toByte(hex));
        }

        public static byte[] toByte(String hexString) {
            int len = hexString.length() / 2;
            byte[] result = new byte[len];
            for (int i = 0; i < len; i++)
                result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
                        16).byteValue();
            return result;
        }

        public static String toHex(byte[] buf) {
            if (buf == null)
                return "";
            StringBuffer result = new StringBuffer(2 * buf.length);
            for (int i = 0; i < buf.length; i++) {
                appendHex(result, buf[i]);
            }
            return result.toString();
        }

        private static void appendHex(StringBuffer sb, byte b) {
            final String HEX = "0123456789ABCDEF";
            sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
        }
    }
}
