package com.xyaxf.axpay;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by John on 13-6-24.
 */
public class Util {

	private static final String TAG = "Util";

	public static InputStream getRawStream(Context context, String rawName) throws IOException {

		int id = context.getResources().getIdentifier(rawName, "raw", context.getPackageName());
		if (id != 0) {
            try{
                return context.getResources().openRawResource(id);
            } catch (Exception e){
                e.printStackTrace();
            }
		}

		throw new IOException(String.format("raw of id: %s from %s not found", id, rawName));
	}

	public static InputStream getDrawableStream(Context context, String rawName) throws IOException {

		int id = context.getResources().getIdentifier(rawName, "drawable", context.getPackageName());
		if (id != 0) {
			BitmapDrawable drawable = (BitmapDrawable) context.getResources().getDrawable(id);
			Bitmap bitmap = drawable.getBitmap();

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 0, os);
			return new ByteArrayInputStream(os.toByteArray());
		}

		throw new IOException(String.format("bitmap of id: %s from %s not found", id, rawName));
	}


	public static String readFile(String path, String encoding) throws IOException {
		InputStream is = getFileStream(path);
		String result = readStreamString(is, encoding);
		is.close();

		return result;
	}

	public static InputStream getFileStream(String path) throws IOException {
		FileInputStream fis = new FileInputStream(path);
		return fis;
	}

	public static InputStream getAssetsStream(Context context, String path) throws IOException {
		InputStream is = context.getAssets().open(path);
		return is;
	}

	public static String readStreamString(InputStream is, String encoding) throws IOException {
		return 	new String(readStream(is), encoding);
	}

	public static void writeStream(InputStream is, OutputStream os) throws IOException {
		byte[] buf = new byte[1024 * 10];
		int readlen;
		while ((readlen = is.read(buf)) >= 0) {
			os.write(buf, 0, readlen);
		}
	}

	public static byte[] readStream(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024 * 10];
		int readlen;
		while ((readlen = is.read(buf)) >= 0) {
			baos.write(buf, 0, readlen);
		}

		baos.close();

		return baos.toByteArray();
	}

	private static final int HTTP_CONNECTION_TIMEOUT = 4 * 1000;
	private static final int HTTP_READ_TIMEOUT = HTTP_CONNECTION_TIMEOUT * 2;

	public static String readHttp(String url, String encoding) throws IOException {
		InputStream is = getHttpStream(url);
		String result = readStreamString(is, encoding);
		is.close();

		return result;

	}

	public static InputStream getHttpStream(String url) throws IOException {
		URLConnection conn = new URL(url).openConnection();

		conn.setConnectTimeout(HTTP_CONNECTION_TIMEOUT);
		conn.setReadTimeout(HTTP_READ_TIMEOUT);
		conn.connect();
		InputStream is = conn.getInputStream();

		return is;
	}

	public static final String HTTP_PREFIX = "http://";
	public static final String HTTPS_PREFIX = "https://";
	public static final String ASSETS_PREFIX = "file://android_assets/";
	public static final String ASSETS_PREFIX2 = "file://android_asset/";
	public static final String ASSETS_PREFIX3 = "assets://";
	public static final String ASSETS_PREFIX4 = "asset://";
	public static final String RAW_PREFIX = "file://android_raw/";
	public static final String RAW_PREFIX2 = "raw://";
	public static final String FILE_PREFIX = "file://";
	public static final String DRAWABLE_PREFIX = "drawable://";

	public static final String[] FILE_URL = {FILE_PREFIX};
	public static final String[] HTTP_URL = {HTTP_PREFIX};
	public static final String[] ASSETS_URL = {ASSETS_PREFIX, ASSETS_PREFIX2, ASSETS_PREFIX3, ASSETS_PREFIX4, };
	public static final String[] RAW_URL = {RAW_PREFIX, RAW_PREFIX2};
	public static final String[] DRAWABLE_URL = {DRAWABLE_PREFIX};

	public static boolean isFileUrl(String url){
		return isUrl(url, FILE_URL);
	}

	public static boolean isLocalUrl(String url){
		return isUrl(url, FILE_URL) || isUrl(url, ASSETS_URL) || isUrl(url, RAW_URL);
	}

	public static boolean isHTTPUrl(String url){
		return isUrl(url, HTTP_URL);
	}

	public static boolean isHTTPSUrl(String url){
		return isUrl(url, new String[]{HTTPS_PREFIX});
	}

	public static boolean isAssetsUrl(String url){
		return isUrl(url, ASSETS_URL);
	}

	public static boolean isRawUrl(String url){
		return isUrl(url, RAW_URL);
	}

	public static boolean isDrawableUrl(String url){
		return isUrl(url, DRAWABLE_URL);
	}

	private static boolean isUrl(String url, String[] prefix){
		for(String s: prefix){
			if(url.startsWith(s)){
				return true;
			}
		}

		return false;
	}

	/**
	 * Get a string from url
	 *
	 * @param url      supported url:
	 *                 http - http://xxx
	 *                 asset file - file://android_asset/xxx
	 *                 raw file - file://android_raw/xxx
	 *                 file - file://xxx
	 * @param encoding
	 * @return
	 */
	public static String getString(Context context, String url, String encoding) throws IOException {
		String result = readStreamString(getStream(context, url), encoding);

		if (result.startsWith("\ufeff")) {
			result = result.substring(1);
		}

		return result;
	}

	public static final String STRING_URL_PREFIX = "string://";
	/**
	 * try to get string from string url like: string://string_name
	 * @param context context
	 * @param textOrStringUrl text or string url
	 * @return text
	 */
	public static String getString(Context context, String textOrStringUrl){
		if(textOrStringUrl.startsWith(STRING_URL_PREFIX)){
			String name = textOrStringUrl.substring(STRING_URL_PREFIX.length());
			int id = context.getResources().getIdentifier(name, "string", context.getPackageName());
			if(id != 0){
				return context.getString(id);
			}
		}

		return textOrStringUrl;
	}

	public static InputStream getStream(Context context, String url) throws IOException {
		String lowerUrl = url.toLowerCase();

		InputStream is;
		if (lowerUrl.startsWith(HTTP_PREFIX)) {
			is = getHttpStream(url);
		} else if (lowerUrl.startsWith(ASSETS_PREFIX)) {
			String assetPath = url.substring(ASSETS_PREFIX.length());
			is = getAssetsStream(context, assetPath);
		} else if (lowerUrl.startsWith(ASSETS_PREFIX2)) {
			String assetPath = url.substring(ASSETS_PREFIX2.length());
			is = getAssetsStream(context, assetPath);
		} else if (lowerUrl.startsWith(ASSETS_PREFIX3)) {
			String assetPath = url.substring(ASSETS_PREFIX3.length());
			is = getAssetsStream(context, assetPath);
		} else if (lowerUrl.startsWith(ASSETS_PREFIX4)) {
			String assetPath = url.substring(ASSETS_PREFIX4.length());
			is = getAssetsStream(context, assetPath);
		} else if (lowerUrl.startsWith(RAW_PREFIX)) {
			String rawName = url.substring(RAW_PREFIX.length());
			is = getRawStream(context, rawName);
		} else if (lowerUrl.startsWith(RAW_PREFIX2)) {
			String rawName = url.substring(RAW_PREFIX2.length());
			is = getRawStream(context, rawName);
		} else if (lowerUrl.startsWith(FILE_PREFIX)) {
			String filePath = url.substring(FILE_PREFIX.length());
			is = getFileStream(filePath);
		} else if (lowerUrl.startsWith(DRAWABLE_PREFIX)) {
			String drawableName = url.substring(DRAWABLE_PREFIX.length());
			is = getDrawableStream(context, drawableName);
		} else {
			throw new IllegalArgumentException(String.format("Unsupported url: %s \n" +
					"Supported: \n%sxxx\n%sxxx\n%sxxx\n%sxxx", url, HTTP_PREFIX, ASSETS_PREFIX, RAW_PREFIX, FILE_PREFIX));
		}

		return is;
	}

	/**
	 * MD5的算法在RFC1321 中定义
	 * 在RFC 1321中，给出了Test suite用来检验你的实现是否正确：
	 * MD5 ("") = d41d8cd98f00b204e9800998ecf8427e
	 * MD5 ("a") = 0cc175b9c0f1b6a831c399e269772661
	 * MD5 ("abc") = 900150983cd24fb0d6963f7d28e17f72
	 * MD5 ("message digest") = f96b697d7cb7938d525a2f31aaf161d0
	 * MD5 ("abcdefghijklmnopqrstuvwxyz") = c3fcd3d76192e4007dfb496cca67e13b
	 *
	 * @author haogj
	 * <p/>
	 * 传入参数：一个字节数组
	 * 传出参数：字节数组的 MD5 结果字符串
	 */
	public static String getMD5(byte[] source) {
		String s = null;
		char hexDigits[] = {       // 用来将字节转换成 16 进制表示的字符
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			md.update(source);
			byte tmp[] = md.digest();
			char str[] = new char[16 * 2];
			int k = 0;
			for (int i = 0; i < 16; i++) {
				byte byte0 = tmp[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			s = new String(str);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static File getTempDir(Context context) {
		File cacheDir;
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
			File appDataDir = new File(Environment.getExternalStorageDirectory(), context.getPackageName());
			cacheDir = new File(appDataDir, "cache");
		}
		else{
			cacheDir = context.getCacheDir();
		}

		File tempDir = new File(cacheDir, "temp");
		tempDir.mkdirs();

		return tempDir;
	}

	public static File createTempFile(Context context) throws IOException {
		return createTempFile(context, "tmp");
	}

	public static File createTempFile(Context context, String extName) throws IOException {
		File tempDir = getTempDir(context);
		File tempFile = new File(tempDir, createTempFileName(extName));

		tempFile.createNewFile();
		tempFile.deleteOnExit();
		return tempFile;
	}

	private static String createTempFileName(String extName) {
		return String.format("%s.%s", System.currentTimeMillis(), extName);
	}

	public static boolean isRemoteUrl(String url) {
		return url.toLowerCase().startsWith("http");
	}

	public static void close(Closeable is) {
		try{
			is.close();
		}
		catch(Exception e){}
	}

	public static boolean isEnabled(String value){
		return !isEmpty(value);
	}

	public static <T>boolean isEnabled(List<T> value){
		return !isEmpty(value);
	}

	public static <T>boolean isEnabled(T[] value){
		return !isEmpty(value);
	}

	public static boolean isEmpty(String text) {
		return text == null || text.trim().length() == 0;
	}

	public static <T>boolean isEmpty(List<T> list){
		return list == null || list.size() == 0;
	}

	public static <T>boolean isEmpty(T[] list){
		return list == null || list.length == 0;
	}


	public static String formatDateTime(long milli){
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		return format.format(new Date(milli));
	}

	public static String formatDate(long milli){
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		return format.format(new Date(milli));
	}

	public static void download(String url, File file) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

		conn.setRequestMethod("GET");
		conn.connect();

		int responseCode = conn.getResponseCode();
		if (!(200 <= responseCode && responseCode < 300)) {
			throw new IOException("Response code: " + responseCode);
		}

		InputStream is = conn.getInputStream();
		if(file.exists()){
			file.delete();
		}

		file.getParentFile().mkdirs();
		file.createNewFile();

		OutputStream os = new FileOutputStream(file);

		byte[] buf = new byte[1024 * 10];
		int readlen;
		while((readlen = is.read(buf)) >= 0){
			os.write(buf, 0, readlen);
		}

		os.close();
		is.close();
	}

	/**
	 * 删除Html标签
	 *
	 * @param inputString
	 * @return
	 */
	public static String removeHtmlTag(String inputString) {
		if (inputString == null){
			return null;
		}

		if(!inputString.contains("<") || !inputString.contains(">")){
			return inputString;
		}

		String htmlStr = inputString; // 含html标签的字符串
		String textStr = "";
		Pattern p_script;
		java.util.regex.Matcher m_script;
		Pattern p_style;
		java.util.regex.Matcher m_style;
		Pattern p_html;
		java.util.regex.Matcher m_html;
		try {
			//定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
			//定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
			String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
			String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll(""); // 过滤script标签
			p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll(""); // 过滤style标签
			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll(""); // 过滤html标签
			textStr = htmlStr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return textStr;// 返回文本字符串
	}

	public static String getRefreshDisplayTime(long time){
		//1 小时内: xx 分钟前
		//24 小时内: xx 小时前
		//7 天内: xx天前
		//日期

		long currentTime = System.currentTimeMillis();
		long timeDiff = (currentTime - time) / 1000;

		final int MINUTE = 60;
		final int HOUR = 60 * MINUTE;
		final int DAY = 24 * 3600;
		final int WEEK = 7 * DAY;
		if(timeDiff > 0){
			if(timeDiff < HOUR){
				int minuteCount = Math.round((float)timeDiff / (float)MINUTE);
				return String.format("%s分钟前", minuteCount == 0? 1: minuteCount);
			}
			else if(timeDiff < DAY){
				int hourCount = Math.round((float)timeDiff / (float)HOUR);
				return String.format("%s小时前", hourCount);
			}
			else if(timeDiff < WEEK){
				int dayCount = Math.round((float)timeDiff / (float)DAY);
				return String.format("%s天前", dayCount);
			}
			else{
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(time);
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH);
				int day = calendar.get(Calendar.DAY_OF_MONTH);

				return String.format("%04d-%02d-%02d", year, month + 1, day);
			}
		}
		else{
			return "-";
		}
	}

	public static boolean equals(Object o1, Object o2){
		if(o1 == o2){
			return true;
		}

		if(o1 != null){
			return o1.equals(o2);
		}

		if(o2 != null){
			return o2.equals(o1);
		}

		return false;
	}

    @Deprecated
    public static String encrypt(Context context, String message){
        throw new RuntimeException("This method is not created");
    }

    @Deprecated
    public static String decrypt(Context context, String encMessage){
        throw new RuntimeException("This method is not created");
    }

    public static String toUTF8String(byte[] data){
        try{
            return new String(data, "UTF-8");
        }
        catch(UnsupportedEncodingException e){
            return null;
        }
    }

    public static byte[] toUTF8ByteArray(String string){
        try {
            return string.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

	public static void showToastLong(Context context, String message){
		showToast(context, message, Toast.LENGTH_LONG);
	}

	public static void showToastShort(Context context, String message){
		showToast(context, message, Toast.LENGTH_SHORT);
	}

	public static void showToastLong(Context context, int messageid){
		showToast(context, context.getResources().getString(messageid), Toast.LENGTH_LONG);
	}

	public static void showToastShort(Context context, int messageid){
		showToast(context, context.getResources().getString(messageid), Toast.LENGTH_SHORT);
	}

	public static void showToast(Context context, String message, int length){
		if (context != null){
			Toast.makeText(context, message, length).show();
		}
	}

	public static PackageInfo getPackageInfo(Context context){
		return getPackageInfo(context, context.getPackageName());
	}

	public static PackageInfo getPackageInfo(Context context, String packageName){
		PackageManager manager = context.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(packageName, 0);
			return info;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static class IllegalInputException extends Exception {
		public IllegalInputException() {
		}

		public IllegalInputException(String detailMessage) {
			super(detailMessage);
		}

		public IllegalInputException(String detailMessage, Throwable throwable) {
			super(detailMessage, throwable);
		}

		public IllegalInputException(Throwable throwable) {
			super(throwable);
		}
	}

	public static String getInputAndCheck(TextView textView, String pattern, String emptyNotify, String formatNotify, boolean doTrim, boolean doFocuse) throws IllegalInputException {
		if(emptyNotify != null && formatNotify == null){
			formatNotify = emptyNotify;
		}
		else if(emptyNotify == null && formatNotify != null){
			emptyNotify = formatNotify;
		}

		String text = textView.getText().toString();
		if(doTrim){
			text = text.trim();
		}

		if(text.length() == 0){
			if(doFocuse){
				textView.requestFocus();
			}
			throw new IllegalInputException(emptyNotify);
		}

		if(pattern != null && !text.matches(pattern)){
			if(doFocuse){
				textView.requestFocus();
			}
			throw new IllegalInputException(formatNotify);
		}

		return text;
	}

	public static String getInputAndCheck(TextView textView, String[] pattern, String emptyNotify, String formatNotify, boolean doTrim, boolean doFocuse) throws IllegalInputException {
		IllegalInputException error = null;
		for(String p: pattern){
			try{
				return getInputAndCheck(textView, p, emptyNotify, formatNotify, doTrim, doFocuse);
			}
			catch(IllegalInputException e){
				error = e;
			}
		}

		throw error;
	}

	public static String formatAmount(float amount, int minDecCount){
		return formatAmount(String.format("%.2f", amount), minDecCount);
	}

	public static String formatAmount(String amount, int minDecCount){
		if(amount == null){
			return null;
		}

		String[] amt = amount.split("\\.");
		if(amt.length == 1){
			String[] formAmt = new String[2];
			formAmt[0] = amt[0];
			formAmt[1] = "";

			amt = formAmt;
		}

		int firstNonZero = amt[0].length(), lastNonZero = -1;
		for(int i = 0; i < amt[0].length(); i ++){
			char ch = amt[0].charAt(i);

			if(ch != '0'){
				firstNonZero = i;
				break;
			}
		}

		for(int i = amt[1].length() - 1; i >= 0; i --){
			char ch = amt[1].charAt(i);

			if(ch != '0'){
				lastNonZero = i;
				break;
			}
		}

		amt[0] = amt[0].substring(firstNonZero);
		amt[1] = amt[1].substring(0, lastNonZero + 1);

		amt[0] = amt[0].length() == 0? "0": amt[0];
		for(int i = 0, count = minDecCount - amt[1].length(); i < count;i ++){
			amt[1] += '0';
		}

		StringBuilder sb = new StringBuilder();
		sb.append(amt[0]);
		if(amt[1].length() > 0){
			sb.append('.').append(amt[1]);
		}

		return sb.toString();
	}

	public static Object clone(Object object){
		return clone(object, object.getClass());
	}

	public static <T>T clone(Object object, Class<T> clazz){
		Gson gson = new Gson();
		String jsonString = gson.toJson(object);
		T newObj = gson.fromJson(jsonString, clazz);
		return newObj;
	}

	/**
	 * google maps的脚本里代码
	 */
	private static final double EARTH_RADIUS = 6378.137;

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	/**
	 * 根据两点间经纬度坐标（double值），计算两点间距离，单位为米
	 */
	public static float getDistance(double lat1, double lng1, double lat2, double lng2) {
		float[] results = new float[1];
		Location.distanceBetween(lat1, lng1, lat2, lng2, results);

		return results[0];
	}

	//longitude belongs to [-180.0d, 180.0d]
	//latitude belongs to [-90.0d, 90.0d]
	public static boolean isValidCoordinate(double longitude, double latitude){
		return Math.abs(longitude) <= 180.0d && Math.abs(latitude) <= 90.0d;
	}

	public static interface Converter<SrcType, DstType>{
		public DstType convert(SrcType obj);
	}
	public static <SrcType, DstType> List<DstType> listConvert(List<SrcType> srcList, Converter<SrcType, DstType> converter){
		ArrayList<DstType> dstList = new ArrayList<DstType>();
		if(srcList != null){
			for(SrcType src: srcList){
				dstList.add(converter.convert(src));
			}
		}

		return dstList;
	}


	public static long getTimeMilliOfToday(int hour, int minute){
		return getTimeMilliOfToday(hour, minute, 0, 0);
	}

	public static long getTimeMilliOfToday(int hour, int minute, int second){
		return getTimeMilliOfToday(hour, minute, second, 0);
	}

	public static long getTimeMilliOfToday(int hour, int minute, int second, int milliSecond){
		Calendar cal = Calendar.getInstance();

		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
		cal.set(Calendar.MILLISECOND, milliSecond);

		return cal.getTimeInMillis();
	}

	public static ComponentName getTopActivityInfo(Context context){
		ActivityManager manager = (ActivityManager) context.getSystemService("activity");//Context.ACTIVITY_SERVICE
		ActivityManager.RunningTaskInfo info = manager.getRunningTasks(1).get(0);
		return info.topActivity;
	}

	public static int[] getTimeItems(String time){
		if(time == null || !(time.matches("[0-9]{14}") || time.matches("[0-9]{8}"))){
			return new int[0];
		}

		boolean dateOnly = time.length() == 8;
		String year = time.substring(0, 4);
		String month = time.substring(4, 6);
		String date = time.substring(6, 8);
		if(dateOnly){
			return new int[]{Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(date)};
			}
		else{
			String hour = time.substring(8, 10);
			String minute = time.substring(10, 12);
			String second = time.substring(12, 14);

			return new int[]{Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(date), Integer.valueOf(hour), Integer.valueOf(minute), Integer.valueOf(second)};
		}
	}

	public static Calendar toCalendar(String time){
		int[] timeItems = getTimeItems(time);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, timeItems[0]);
		cal.set(Calendar.MONTH, timeItems[1] - 1);
		cal.set(Calendar.DAY_OF_MONTH, timeItems[2]);
		cal.set(Calendar.HOUR_OF_DAY, timeItems.length == 6? timeItems[3]: 0);
		cal.set(Calendar.MINUTE, timeItems.length == 6 ? timeItems[4] : 0);
		cal.set(Calendar.SECOND, timeItems.length == 6? timeItems[5]: 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal;
	}

	public static int[] getTimeItems(Calendar calendar){
		return new int[]{
				calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH) + 1,
				calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE),
				calendar.get(Calendar.SECOND),
		};
	}

	public static Calendar createCalendar(int[] timeItems){
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, timeItems[0]);
		c.set(Calendar.MONTH, timeItems[1] - 1);
		c.set(Calendar.DAY_OF_MONTH, timeItems[2]);
		c.set(Calendar.HOUR_OF_DAY, timeItems.length >= 4? timeItems[3]: 0);
		c.set(Calendar.MINUTE, timeItems.length >= 5? timeItems[4]: 0);
		c.set(Calendar.SECOND, timeItems.length >= 6? timeItems[5]: 0);

		return c;
	}

	public static String getCurrentTime(){
		return new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis());
	}

	public static int getDateDiff(int[] ldate, int[] rdate){
		return getDateDiff(createCalendar(ldate), createCalendar(rdate));
	}

	public static int getDateDiff(Calendar ldate, Calendar rdate){
		ldate = (Calendar) ldate.clone();
		rdate = (Calendar) rdate.clone();

		ldate.set(Calendar.HOUR_OF_DAY, 0);
		ldate.set(Calendar.MINUTE, 0);
		ldate.set(Calendar.SECOND, 0);
		ldate.set(Calendar.MILLISECOND, 0);

		rdate.set(Calendar.HOUR_OF_DAY, 0);
		rdate.set(Calendar.MINUTE, 0);
		rdate.set(Calendar.SECOND, 0);
		rdate.set(Calendar.MILLISECOND, 0);

		long dMilli = ldate.getTimeInMillis() - rdate.getTimeInMillis();

		return (int) Math.round((double)(dMilli / 1000) / (double)(60 * 60 * 24));
	}

	public static int str2Int(String strValue, int defaultValue){
		if(strValue != null){
			try{
				return Integer.valueOf(strValue);
			}
			catch(NumberFormatException e){
				// do nothing
			}
		}

		return defaultValue;
	}

	public static float str2Float(String strValue, float defaultValue){
		if(strValue != null){
			try{
				return Float.valueOf(strValue);
			}
			catch(NumberFormatException e){
				// do nothing
			}
		}

		return defaultValue;
	}

	public static double str2Double(String strValue, double defaultValue){
		if(strValue != null){
			try{
				return Double.valueOf(strValue);
			}
			catch(NumberFormatException e){
				// do nothing
			}
		}

		return defaultValue;
	}

	public static Map<String, String> parseKVRaw(String kvRaw, String encoding){
		HashMap<String, String> result = new HashMap<String, String>();
		String[] kvArray = kvRaw.split("&");
		for(String kv: kvArray){
			String[] keyValue = kv.split("=");
			if(keyValue.length == 2){
				String key, value;
				key = keyValue[0];
				try {
					value = URLDecoder.decode(keyValue[1], encoding);
				} catch (UnsupportedEncodingException e) {
					continue;
				}

				result.put(key, value);
			}
		}

		return result;
	}

	public static int parseInteger(String value, int defaultValue){
		try{
			return Integer.valueOf(value);
		}
		catch(Exception e){
			return defaultValue;
		}
	}

	public static long parseLong(String value, long defaultValue){
		try{
			return Long.valueOf(value);
		}
		catch(Exception e){
			return defaultValue;
		}
	}

	public static float parseFloat(String value, float defaultValue){
		try{
			return Float.valueOf(value);
		}
		catch(Exception e){
			return defaultValue;
		}
	}

	public static double parseDouble(String value, double defaultValue){
		try{
			return Double.valueOf(value);
		}
		catch(Exception e){
			return defaultValue;
		}
	}

	public static int getDaysDiff(String startTime , String endTime){
		Calendar startCal = toCalendar(startTime.substring(0, 8));
		Calendar endCal = toCalendar(endTime.substring(0, 8));

		double milliDiff = endCal.getTimeInMillis() - startCal.getTimeInMillis();

		double days = milliDiff / 1000d / 24d / 3600d;

		return (int) Math.round(days);
	}

	public static void dumpCursor(Cursor c){
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for(int i = 0; i < c.getColumnCount(); i ++){
			String column = c.getColumnName(i);
			String value = c.getString(i);

			sb.append(String.format("%s=[%s], ", column, value));
		}

		sb.append("}");

		Log.d(TAG, sb.toString());
	}

	public static String getUtf8String(byte[] data){
		try{
			return new String(data, "utf-8");
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	public static byte[] getUtf8Bytes(String str){
		try {
			return str.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			return new byte[0];
		}
	}

	public static byte[] hexStr2Byte(String hexStr){
		if(hexStr == null || hexStr.length() == 0){
			return new byte[0];
		}

		if(hexStr.length() % 2 != 0){
			hexStr = "0" + hexStr;
		}

		byte[] result = new byte[hexStr.length() / 2];
		for(int i = 0; i < hexStr.length() / 2; i ++){
			String bHexStr = hexStr.substring(i * 2, i * 2 + 2);


			result[i] = (byte)(0x00ff & Integer.valueOf(bHexStr, 16));
		}

		return result;
	}

	public static String byte2HexStr(byte[] data){
		if(data == null){
			return "";
		}

		StringBuilder sb = new StringBuilder();

		for(byte b: data){
			String bHexStr = Integer.toHexString(0x00ff & b);
			if(bHexStr.length() == 1){
				sb.append('0');
			}

			sb.append(bHexStr);

		}

		return sb.toString();
	}

	public static int byte2Int(byte[] data){
		byte[] formData = new byte[4];
		System.arraycopy(data, 0, formData, 0, Math.min(data.length, formData.length));

		int result = 0x00000000;
		result |= (0x000000ff & formData[0]); result <<= 8;
		result |= (0x000000ff & formData[1]); result <<= 8;
		result |= (0x000000ff & formData[2]); result <<= 8;
		result |= (0x000000ff & formData[3]);

		return result;
	}

	public static byte[] int2Byte(int val){
		byte[] result = new byte[4];

		result[3] = (byte)(0x000000ff & val); val >>= 8;
		result[2] = (byte)(0x000000ff & val); val >>= 8;
		result[1] = (byte)(0x000000ff & val); val >>= 8;
		result[0] = (byte)(0x000000ff & val);

		return result;
	}

	public static long byte2Long(byte[] data){
		byte[] formData = new byte[8];
		System.arraycopy(data, 0, formData, 0, Math.min(data.length, formData.length));

		long result = 0x0;
		result |= (0x00ffl & formData[0]); result <<= 8;
		result |= (0x00ffl & formData[1]); result <<= 8;
		result |= (0x00ffl & formData[2]); result <<= 8;
		result |= (0x00ffl & formData[3]); result <<= 8;
		result |= (0x00ffl & formData[4]); result <<= 8;
		result |= (0x00ffl & formData[5]); result <<= 8;
		result |= (0x00ffl & formData[6]); result <<= 8;
		result |= (0x00ffl & formData[7]);

		return result;
	}

	public static byte[] long2Byte(long val){
		byte[] result = new byte[8];

		result[7] = (byte)(0x000000ff & val); val >>= 8;
		result[6] = (byte)(0x000000ff & val); val >>= 8;
		result[5] = (byte)(0x000000ff & val); val >>= 8;
		result[4] = (byte)(0x000000ff & val); val >>= 8;
		result[3] = (byte)(0x000000ff & val); val >>= 8;
		result[2] = (byte)(0x000000ff & val); val >>= 8;
		result[1] = (byte)(0x000000ff & val); val >>= 8;
		result[0] = (byte)(0x000000ff & val);

		return result;
	}

	public static String getStackStackTrace(){
		Thread currentThread = Thread.currentThread();
		StackTraceElement[] trace = currentThread.getStackTrace();

		StringBuilder sb = new StringBuilder();
		sb.append(currentThread.toString());

		if(trace != null){
			for(StackTraceElement e: trace){
				sb.append(e.toString()).append('\n');
			}
		}

		return sb.toString();
	}

	public static String getMetaValue(Context context, String metaKey) {
		Bundle metaData = null;
		String value = null;
		if (context == null || metaKey == null) {
			return null;
		}
		try {
			ApplicationInfo ai = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			if (null != ai) {
				metaData = ai.metaData;
			}
			if (null != metaData) {
				value = metaData.getString(metaKey);
			}
		} catch (PackageManager.NameNotFoundException e) {

		}
		return value;
	}

	public static String getFirstNonEmpty(String... values){
		for(String value: values){
			if(isEnabled(value)){
				return value;
			}
		}

		return null;
	}

	public static OutputStream createSlowOutputStream(final OutputStream os){
		return new OutputStream() {
			@Override
			public void write(int oneByte) throws IOException {
				try{
					Thread.sleep(50);
				}
				catch(Exception ignore){}

				os.write(oneByte);
			}
		};
	}

	public static InputStream createSlowInputStream(final InputStream is){
		return new InputStream() {
			@Override
			public int read() throws IOException {
				try{
					Thread.sleep(50);
				}
				catch(Exception ignore){}

				return is.read();
			}
		};
	}

	public static boolean isInForgbround(Context context){
		ActivityManager am= (ActivityManager) context.getSystemService("activity");//Context.ACTIVITY_SERVICE
		List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);

		ActivityManager.RunningTaskInfo rti = runningTasks.get(0);

		String topActivity = rti.topActivity.getClassName();
		String topPackage = rti.topActivity.getPackageName();
		boolean result = context.getPackageName().equals(rti.topActivity.getPackageName());

		return result;

	}

	public static String getNetworkType(Context context){
		TelephonyManager tm = (TelephonyManager) context.getSystemService("phone");//Context.TELEPHONY_SERVICE
		switch (tm.getNetworkType()) {
			case TelephonyManager.NETWORK_TYPE_GPRS:
				return "GPRS";
			case TelephonyManager.NETWORK_TYPE_EDGE:
				return "EDGE";
			case TelephonyManager.NETWORK_TYPE_UMTS:
				return "UMTS";
			case TelephonyManager.NETWORK_TYPE_CDMA:
				return "CDMA";
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
				return "EVDO_0";
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
				return "EVDO_A";
			case TelephonyManager.NETWORK_TYPE_1xRTT:
				return "1xRTT";
			case TelephonyManager.NETWORK_TYPE_HSDPA:
				return "HSDPA";
			case TelephonyManager.NETWORK_TYPE_HSUPA:
				return "HSUPA";
			case TelephonyManager.NETWORK_TYPE_HSPA:
				return "HSPA";
			case TelephonyManager.NETWORK_TYPE_IDEN:
				return "IDEN";
			case TelephonyManager.NETWORK_TYPE_EVDO_B:
				return "EVDO_B";
			case TelephonyManager.NETWORK_TYPE_LTE:
				return "LTE";
			case TelephonyManager.NETWORK_TYPE_EHRPD:
				return "EHRPD";
			case TelephonyManager.NETWORK_TYPE_HSPAP:
				return "HSPAP";
			case 16: //TelephonyManager.NETWORK_TYPE_GSM:
				return "GSM";
			case 17: //TelephonyManager.NETWORK_TYPE_TD_SCDMA:
				return "TD_SCDMA";
			case 18: //TelephonyManager.NETWORK_TYPE_IWLAN:
				return "IWLAN";
			case TelephonyManager.NETWORK_TYPE_UNKNOWN:
			default:
				return "UNKNOWN";
		}
	}

	public static void myShowToast(final Activity activity, final String word, final long time) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				final Toast toast = Toast.makeText(activity, word, Toast.LENGTH_LONG);
				toast.show();
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {
						toast.cancel();
					}
				}, time);
			}
		});
	}

	public static long downloadGYAPKFile(Context context, String dirName , String url){
		File dirFile = new File(Environment.getExternalStorageDirectory().getPath()+
				File.separator+ dirName);
		if(!dirFile.isDirectory()){
			dirFile.mkdir();
		}
		File[] files = dirFile.listFiles();
		for (File file : files){
			file.delete();
		}

		//创建下载任务,downloadUrl就是下载链接
		DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
		//指定下载路径和下载文件名
		request.setDestinationInExternalPublicDir(dirName, "axinfu_creditpay_sdk.apk");
		//获取下载管理器
		DownloadManager downloadManager= (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
		//将下载任务加入下载队列，否则不会进行下载
		return downloadManager.enqueue(request);
	}
}
