package net.bestidear.bestidearlauncher.utils;

import java.security.MessageDigest;

public class Md5 {
	private static String byte2HexStr(byte paramByte) {
		int i = paramByte;
		if (i < 0)
			i = paramByte & 0xFF;
		return (Integer.toHexString(i / 16) + Integer.toHexString(i % 16))
				.toUpperCase();
	}

	private static String byteArray2HexStr(byte[] paramArrayOfByte) {
		StringBuffer localStringBuffer = new StringBuffer();
		int i = paramArrayOfByte.length;
		for (int j = 0; j < i; j++)
			localStringBuffer.append(byte2HexStr(paramArrayOfByte[j]));
		return localStringBuffer.toString();
	}

	public static String getMD5ofStr(String paramString) {
		try {
			String str = byteArray2HexStr(MessageDigest.getInstance("MD5")
					.digest(paramString.getBytes()));
			return str;
		} catch (Exception localException) {

			localException.printStackTrace();
			return null;
		}
	}

	public static String getMD5ofStr(String paramString, int paramInt) {
		String str = getMD5ofStr(paramString);
		for (int i = 0; i < paramInt + -1; i++)
			str = getMD5ofStr(str);
		return getMD5ofStr(str);
	}

	public static boolean verifyPassword(String paramString1,
			String paramString2) {
		return getMD5ofStr(paramString1).equals(paramString2);
	}

	public static boolean verifyPassword(String paramString1,
			String paramString2, int paramInt) {
		return getMD5ofStr(paramString1, paramInt).equals(paramString2);
	}
}