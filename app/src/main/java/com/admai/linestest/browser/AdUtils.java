package com.admai.linestest.browser;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

public class AdUtils {
    public static String SHA1(String s) {
        try {
            MessageDigest disgest = MessageDigest.getInstance("SHA-1");
            disgest.update(s.getBytes());
            byte messageDigest[] = disgest.digest();
            return toHexString(messageDigest);
        } catch (NoSuchAlgorithmException e) {
           
                e.printStackTrace();
        }
        return "";
    }
                  

    public static String concatString(Object... paras) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < paras.length; i++) {
            sb.append(paras[i]);
        }
        return sb.toString();
    }


    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        try {
            InputStreamReader isr = new InputStreamReader(is);
            char[] inputBuffer = new char[1000];
            int len = -1;
            while ((len = isr.read(inputBuffer)) > -1) {
                sb.append(inputBuffer, 0, len);
            }
        } catch (IOException e) {
            // LogUtil.addErrorLog("convertStreamToString:"+ e.toString());
        } finally {
            closeStream(is);
            try {
                reader.close();
            } catch (IOException e) {
                    e.printStackTrace();
            }
        }

        return sb.toString();
    }


    public static String convertStreamToString(InputStream is,
                                               boolean wantToClose) {
        StringBuffer out = new StringBuffer();
        try {
            byte[] b = new byte[4096];
            for (int n; (n = is.read(b)) != -1; ) {
                out.append(new String(b, 0, n));
            }
        } catch (IOException e) {
            // LogUtil.addErrorLog("convertStreamToString:"+ e.toString());
        }
        return out.toString();
    }


    public static byte[] convertStreamToByte(InputStream in) throws IOException {

        //        StringBuilder stringBuilder = new StringBuilder();
        //
        //        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        //        int b;
        //        while ((b = br.read()) != -1) {
        //            stringBuilder.append(b);
        //        }
        //
        //        return stringBuilder.toString().getBytes();


        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int count = -1;
        while ((count = in.read(data)) != -1) {
            outStream.write(data, 0, count);
        }

        data = null;
        byte[] rs = outStream.toByteArray();
        in.close();
        outStream.flush();
        outStream.close();
        return rs;
    }

    public static Bitmap loadAssetsBitmap(Context context, String filePath) {

        InputStream is = null;
        Bitmap bitmap = null;

        try {
            //            if (AdManager.getImageSource().containsKey(filePath)) {
            ////                int sourceId = AdManager.getImageSource().get(filePath).intValue();
            //                int sourceId = AdManager.getImageSource().get(filePath);
            //                if (AdUtils.isExists(context.getResources(), sourceId)) {
            //                    bitmap = BitmapFactory.decodeResource(context.getResources(), sourceId);
            //                }
            //            }

            if (bitmap == null) {
                is = loadAssetsInputStream(context, filePath);
                bitmap = convertStreamToBitmap(is);
            }
        } catch (IOException e) {

        }
        return bitmap;
    }

    public static InputStream loadAssetsInputStream(Context context, String filePath) {
        InputStream is = null;
        try {
            is = context.getAssets().open(filePath);
        } catch (Exception e) {
        }
        return is;
    }

    public static Bitmap convertStreamToBitmap(InputStream is)
        throws NullPointerException, IOException {
        Bitmap bmp = null;
        // try {
        // ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // int ch;
        // while ((ch = is.read()) != -1)
        // stream.write(ch);
        // byte imgdata[] = stream.toByteArray();
        // closeStream(is);
        // bmp = BitmapFactory.decodeByteArray(imgdata, 0, imgdata.length);
        // } catch (Exception e) {
        // }
        //
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        bmp = BitmapFactory.decodeStream(is, null, options);
        int width = options.outWidth;
        int height = options.outHeight;
        return bmp;
    }

    public static Bitmap convertByteArrayTobitmap(byte[] imgdata) {
        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeByteArray(imgdata, 0, imgdata.length);
        } catch (Exception e) {

        }
        return bmp;
    }

    public static void closeStream(OutputStream stream) {
        try {
            if (stream != null) {
                stream.flush();
                stream.close();
            }
        } catch (IOException e) {

        }
    }

    public static void closeStream(InputStream stream) {
        try {
            if (stream != null) {
                stream.close();
            }
        } catch (IOException e) {

        }
    }

    public static Bitmap big(Bitmap bitmap, float scale) {
        if (bitmap == null) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap resizeBmp = Bitmap
                               .createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return resizeBmp;
    }

    /**
     * 是否考虑旋转屏幕的情况
     */
    public static boolean isRelateScreenRotate = true;

    public static Bitmap getBitmap(Context context, Bitmap bmp, int width, int height) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screenwidth;
        int screenheight;
        if (isRelateScreenRotate) {
            screenwidth = displayMetrics.widthPixels;
            screenheight = displayMetrics.heightPixels;
        } else {
            if (displayMetrics.widthPixels >= displayMetrics.heightPixels) {
                screenwidth = displayMetrics.heightPixels;
                screenheight = displayMetrics.widthPixels;
            } else {
                screenwidth = displayMetrics.widthPixels;
                screenheight = displayMetrics.heightPixels;
            }
        }

        int dwidth = bmp.getWidth();
        int dheight = bmp.getHeight();

        int rswidth = screenwidth < dwidth ? screenwidth : dwidth;
        int rsheight = screenheight < dheight ? screenheight : dheight;

        if (width > 0) {
            rswidth = rswidth < width ? rswidth : width;
        }
        if (height > 0) {
            rsheight = rsheight < height ? rsheight : height;
        }

        int newWidth = rswidth;
        int newHeight = rsheight;

        if (rswidth * dheight / dwidth < rsheight) {
            newHeight = rswidth * dheight / dwidth;
        } else {
            newWidth = rsheight * dwidth / dheight;
        }
        Bitmap bt = bmp;
        if (newHeight != dheight || newWidth != dwidth) {
            bt = Bitmap.createScaledBitmap(bmp, newWidth, newHeight, false);
            if (!bmp.isRecycled()) {
                bmp.recycle();
            }
        }
        return bt;
    }

    public final static String KSplitTag = "|||";

    public static void splitTrackUrl(String urls, LinkedList<String> list) {
        list.clear();
        int start = 0;
        while (true) {
            int index = urls.indexOf(KSplitTag, start);
            if (-1 == index) {
                String subOne = urls.substring(start);// + "&r=" +
                // GetRandomNumber();
                if (subOne.length() > 0) {
                    list.add(subOne);
                }
                break;
            }

            String subOne = urls.substring(start, index);// + "&r=" +
            // GetRandomNumber();
            if (subOne.length() > 0) {
                list.add(subOne);
            }
            start = index + KSplitTag.length();
        }
    }

    public static int GetRandomNumber() {
        int random = (int) (Math.random() * 10000);
        return random;
    }

    public static String getNowTime(String format) {
        Date now = new Date();
        Format formatter = new SimpleDateFormat(format);
        return formatter.format(now);
    }

    public static boolean isWifiOrNotRoaming3G(Context context) {
        TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        if (info == null) {
            return false;
        }


        int netType = info.getType();

        int netSubtype = info.getSubtype();
        if (netType == ConnectivityManager.TYPE_WIFI) {
            return info.isConnected();
        }

        String connStatus = concatString(info.getTypeName(), ",",
                                         info.getSubtypeName()).toUpperCase();
        if (netType == ConnectivityManager.TYPE_MOBILE
            && (connStatus.contains("UMTS")
                || connStatus.contains("EVDO")
                || connStatus.contains("WCDMA")
                || connStatus.contains("HSDPA")
                || connStatus.contains("HSUPA"))
            && !mTelephony.isNetworkRoaming()) {
            return info.isConnected();
        } else {
            return false;
        }
    }

    //    public static String getNetworkTypes(Context context) {
    //        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    //        NetworkInfo[] networkinfos = connManager.getAllNetworkInfo();
    //        StringBuffer types = new StringBuffer();
    //
    //        if (networkinfos != null) {
    //            for (NetworkInfo netInfo : networkinfos) {
    //                if (netInfo.isConnected()) {
    //                    types.append(",");
    //                    types.append(netInfo.getTypeName());
    //                    types.append(",");
    //                    types.append(netInfo.getSubtypeName());
    //                }
    //            }
    //        }
    //        if (types.length() > 0)
    //            return types.substring(1);
    //        return "";
    //    }

    public static String getMnc(Context mContext) {
        String mnc = "";
        TelephonyManager telephoneyManager = (TelephonyManager) mContext
                                                                    .getSystemService(Context.TELEPHONY_SERVICE);
        String MC = telephoneyManager.getNetworkOperator();
        if (MC.length() >= 5) {
            mnc = MC.substring(3, 5);
        }
        return mnc;
    }

    public static String getActiveNetworkType(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        if (info == null) {
            return "";
        }
        StringBuffer type = new StringBuffer();
        if (info.isConnected()) {
            type.append(info.getTypeName());
            if (info.getSubtypeName().length() > 0) {
                type.append(",");
                type.append(info.getSubtypeName());
            }
        }
        return type.toString();
    }

    public static boolean isCachedFileTimeout(String filename) {
        try {
            return isCachedFileTimeout(filename, 7);
        } catch (ParseException e) {
            // LogUtil.addErrorLog("isCachedFileTimeout:" + e.toString());
        }
        return true;
    }

    public static boolean isCachedFileTimeout(String filename, int days)
        throws ParseException {
        // filename  yyyyMMddHHmmss*.tmp
        int length = "yyyyMMddHHmmss".length();
        if (filename.length() < length) {
            return true;
        }

        String date = filename.substring(0, length);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date time = formatter.parse(date);

        Calendar cTime = Calendar.getInstance();
        cTime.setTime(time);
        cTime.add(Calendar.DATE, days);

        Date now = new Date();
        return cTime.getTime().before(now);
    }

    public static boolean isTimeOut(String timeStamp, int days) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            Date time = formatter.parse(timeStamp);
            Calendar cTime = Calendar.getInstance();
            cTime.setTime(time);
            cTime.add(Calendar.DATE, days);

            Date now = new Date();
            return cTime.getTime().before(now);
        } catch (ParseException e) {

                e.printStackTrace();
        }
        return true;

    }

    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int devicePixelToMraidPoint(int pixelSize, float scale) {
        int points = Math.round(pixelSize / scale);
        return points;
    }

    public static int mraidPointToDevicePixel(int pointSize, float scale) {
        int pixels = Math.round(pointSize * scale);
        return pixels;
    }

    public static void setWindowBackgroundColor(Window window, int color, int opacity) {
        color = color | (opacity << 24);
        ColorDrawable drawable = new ColorDrawable(color);
        window.setBackgroundDrawable(drawable);
    }

    public static String getIMEI(Context context) {
        TelephonyManager mgr = (TelephonyManager) context
                                                      .getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = mgr.getDeviceId();
        if (deviceId != null) {
            return deviceId;
        }
        return "";
    }

    public static String getAndroidId(Context context) {
        String android_id = Secure.getString(context.getContentResolver(),
                                             Secure.ANDROID_ID);
        if (android_id != null) {
            return android_id;
        }
        return "";
    }

    public static String getMacAddress(Context context) {
        WifiManager wm = (WifiManager) context
                                           .getSystemService(Context.WIFI_SERVICE);
        if (wm != null && wm.getConnectionInfo() != null && wm.getConnectionInfo().getMacAddress() != null) {
            String macAddress = wm.getConnectionInfo().getMacAddress()
                                  .replace(":", "");
            return macAddress.toUpperCase();
        }
        return "";
    }

    public static String getIpAddress(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            String ip = ((ipAddress >> 24) & 0xFF) + "."
                        + ((ipAddress >> 16) & 0xFF) + "."
                        + ((ipAddress >> 8) & 0xFF) + "." + (ipAddress & 0xFF);
            return ip;
        } catch (Exception e) {
                e.printStackTrace();
        }
        return "";
    }

    public String intToIp(int i) {
        return ((i >> 24) & 0xFF) + "." + ((i >> 16) & 0xFF) + "."
               + ((i >> 8) & 0xFF) + "." + (i & 0xFF);
    }

    private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5',
                                              '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        return sb.toString();
    }


    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = SDCardAccessAble();
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
            return sdDir.toString();
        }
        return "";
    }

    public static boolean SDCardAccessAble() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }


    public static boolean isExists(Resources resources, int resid) {
        try {
            resources.getResourceName(resid);
        } catch (NotFoundException e) {
            return false;
        }
        return true;
    }


    public static String getJsonValue(JSONObject object, String key) {
        String value = "";
        try {
            value = object.getString(key);
        } catch (JSONException e) {
            return value;
        }
        return value;
    }

    public static Long getJsonLong(JSONObject object, String key) {
        Long value = null;
        try {
            value = object.getLong(key);
        } catch (JSONException e) {
            return value;
        }
        return value;
    }


    public static void setSuitableBitmap(ImageView imageView, Bitmap bmp) {
        try {
            imageView.setImageBitmap(bmp);
        } catch (OutOfMemoryError err) {

        }
    }

    public static boolean checkedPermission(Context context, String permission) {
        boolean rs = false;
        try {
            rs = (context.checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        } catch (Exception e) {
                e.printStackTrace();
            return false;
        }
        return rs;
    }

    public static String getResolution(Context context,
                                       boolean isRelateScreenRotate) {
        String resolution = "";
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        if (isRelateScreenRotate) {
            resolution = AdUtils.concatString(displayMetrics.widthPixels, "x", displayMetrics.heightPixels);
        } else {
            if (displayMetrics.widthPixels >= displayMetrics.heightPixels) {
                resolution = AdUtils.concatString(displayMetrics.heightPixels, "x", displayMetrics.widthPixels);
            } else {
                resolution = AdUtils.concatString(displayMetrics.widthPixels, "x", displayMetrics.heightPixels);
            }
        }

        return resolution;
    }

    public static int getOrientation(Context context) {
        int orientation = 0;
        if (context.getResources()
                   .getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            orientation = 2;
        } else if (context.getResources()
                          .getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            orientation = 1;
        }
        return orientation;
    }

    public static boolean isUserAllowOrientation(Context mContext) {

        if ((mContext instanceof Activity) == false) {
            return false;
        }
        try {
            ComponentName name = ((Activity) mContext).getComponentName();
            ActivityInfo info = ((Activity) mContext).getPackageManager()
                                                     .getActivityInfo(name, PackageManager.GET_META_DATA);
            // LogUtil.addLog("config orientation = "+info.screenOrientation);
            if (info.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                || info.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR) {

                return true;
            } else {

            }
        } catch (NameNotFoundException e) {
            return false;
        }
        return false;
    }

    public static boolean isPermission(Context context, String paramString) {
        PackageManager localPackageManager = context.getPackageManager();
        return localPackageManager.checkPermission(paramString, context.getPackageName()) == 0;
    }
}
