package com.admai.linestest.browser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.MailTo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.webkit.JsResult;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class WebViewUtils {

    /**
     * 在SDK中打开URL
     *
     * @param context
     * @param targetUrl
     * @param
     */
    public static void openPageInSDK(Context context, String targetUrl, String info) {
        Intent myIntent = new Intent();
        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle b = new Bundle();
        b.putString("browserurl", targetUrl);
        b.putBoolean("istransparent", true);
        if (!TextUtils.isEmpty(info)) {
            b.putString("info", info);
        }             
        myIntent.putExtras(b);
        myIntent.setClass(context, AdBrowserView.class);
        context.startActivity(myIntent);
        ((Activity) context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    
    }

    /**
     * 页面跳转
     *
     * @param view
     * @param url
     */
    public static void goToPage(WebView view, String url) {
        Map<String, String> extraHeaders = new HashMap<String, String>();
        if (view.getUrl() != null)
            extraHeaders.put("Referer", view.getUrl());
        view.loadUrl(url, extraHeaders);
    }

    public static void JSAlert(Context context, String message, final JsResult result) {

        //创建Dialog
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(message).setPositiveButton("确定",
                        new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        });

        alertDialog.setCancelable(false);
        alertDialog.create();
        alertDialog.show();
    }

    public final static String KEnc = "utf-8";

    //***************************各类Intent*******************************

    public static Intent createVideoPlayIntent(String url) throws UnsupportedEncodingException {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.parse(URLDecoder.decode(url, KEnc));
        i.setDataAndType(uri, "video/mp4");
        return i;
    }


    /**
     * 创建邮件Intent
     *
     * @param url
     * @return
     * @throws UnsupportedEncodingException
     */
    public static Intent createMailIntent(String url) throws UnsupportedEncodingException {
        url = URLDecoder.decode(url, KEnc);
        MailTo mt = MailTo.parse(url);
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{mt.getTo()});
        i.putExtra(Intent.EXTRA_SUBJECT, mt.getSubject());
        i.putExtra(Intent.EXTRA_CC, mt.getCc());
        i.putExtra(Intent.EXTRA_TEXT, mt.getBody());
        return i;
    }

    /**
     * 创建短信Intent
     *
     * @param url
     * @return
     * @throws UnsupportedEncodingException
     */
    public static Intent createSmsIntent(String url) throws UnsupportedEncodingException {
        url = URLDecoder.decode(url, KEnc);
        String phoneNumber = "";
        String smsBody = "";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setType("vnd.android-dir/mms-sms");
        int index1 = url.indexOf("?");
        if (index1 > 0) {
            phoneNumber = url
                    .substring(4, url.indexOf("?"));
        } else {
            phoneNumber = url.substring(4);
        }

        int index2 = url.indexOf("=");
        if (index2 > 0) {
            smsBody = url.substring(url.indexOf("=") + 1);
        }
        i.putExtra("address", phoneNumber);
        i.putExtra("sms_body", smsBody);
        return i;
    }

    /**
     * 创建打电话Intent
     *
     * @param uri
     * @return
     */
    public static Intent createTelIntent(Uri uri) {
        //可以使用ACTION_CALL直接打电话,但是跳转到拨号更好
        Intent i = new Intent(Intent.ACTION_DIAL);
        i.setData(uri);
        return i;
    }

    /**
     * 创建新Activty Intent
     *
     * @param uri
     * @return
     */
    public static Intent createNewActivtyIntent(Uri uri) {
        Intent i = new Intent(Intent.ACTION_VIEW, uri);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return i;
    }

    /**
     * 判断sd卡是否存在并可读写
     *
     * @return
     */
    public static boolean SDCardAccessAble() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static String getJsonValue(JSONObject object, String key){
        String value = "";
        try {
            value = object.getString(key);
        } catch (JSONException e) {
            return value;
        }
        return value;
    }



}
