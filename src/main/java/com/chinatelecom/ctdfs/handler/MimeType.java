package com.chinatelecom.ctdfs.handler;


import java.util.HashMap;

public class MimeType {
	
	private static HashMap<String,String> mimeMap=new  HashMap<String,String>();
	
	private static HashMap<String,Boolean> inlineMap=new  HashMap<String,Boolean>();
	
	static{
		mimeMap.put("evy","application/envoy");
		mimeMap.put("fif","application/fractals");
		mimeMap.put("spl","application/futuresplash");
		mimeMap.put("hta","application/hta");
		mimeMap.put("acx","application/internet-property-stream");
		mimeMap.put("hqx","application/mac-binhex40");
		mimeMap.put("doc","application/msword");
		mimeMap.put("dot","application/msword");
		mimeMap.put("*","application/octet-stream");
		mimeMap.put("bin","application/octet-stream");
		mimeMap.put("class","application/octet-stream");
		mimeMap.put("dms","application/octet-stream");
		mimeMap.put("exe","application/octet-stream");
		mimeMap.put("lha","application/octet-stream");
		mimeMap.put("lzh","application/octet-stream");
		mimeMap.put("oda","application/oda");
		mimeMap.put("axs","application/olescript");
		mimeMap.put("pdf","application/pdf");
		mimeMap.put("prf","application/pics-rules");
		mimeMap.put("p10","application/pkcs10");
		mimeMap.put("crl","application/pkix-crl");
		mimeMap.put("ai","application/postscript");
		mimeMap.put("eps","application/postscript");
		mimeMap.put("ps","application/postscript");
		mimeMap.put("rtf","application/rtf");
		mimeMap.put("setpay","application/set-payment-initiation");
		mimeMap.put("setreg","application/set-registration-initiation");
		mimeMap.put("xla","application/vnd.ms-excel");
		mimeMap.put("xlc","application/vnd.ms-excel");
		mimeMap.put("xlm","application/vnd.ms-excel");
		mimeMap.put("xls","application/vnd.ms-excel");
		mimeMap.put("xlt","application/vnd.ms-excel");
		mimeMap.put("xlw","application/vnd.ms-excel");
		mimeMap.put("msg","application/vnd.ms-outlook");
		mimeMap.put("sst","application/vnd.ms-pkicertstore");
		mimeMap.put("cat","application/vnd.ms-pkiseccat");
		mimeMap.put("stl","application/vnd.ms-pkistl");
		mimeMap.put("pot","application/vnd.ms-powerpoint");
		mimeMap.put("pps","application/vnd.ms-powerpoint");
		mimeMap.put("ppt","application/vnd.ms-powerpoint");
		mimeMap.put("mpp","application/vnd.ms-project");
		mimeMap.put("wcm","application/vnd.ms-works");
		mimeMap.put("wdb","application/vnd.ms-works");
		mimeMap.put("wks","application/vnd.ms-works");
		mimeMap.put("wps","application/vnd.ms-works");
		mimeMap.put("hlp","application/winhlp");
		mimeMap.put("bcpio","application/x-bcpio");
		mimeMap.put("cdf","application/x-cdf");
		mimeMap.put("z","application/x-compress");
		mimeMap.put("tgz","application/x-compressed");
		mimeMap.put("cpio","application/x-cpio");
		mimeMap.put("csh","application/x-csh");
		mimeMap.put("dcr","application/x-director");
		mimeMap.put("dir","application/x-director");
		mimeMap.put("dxr","application/x-director");
		mimeMap.put("dvi","application/x-dvi");
		mimeMap.put("gtar","application/x-gtar");
		mimeMap.put("gz","application/x-gzip");
		mimeMap.put("hdf","application/x-hdf");
		mimeMap.put("ins","application/x-internet-signup");
		mimeMap.put("isp","application/x-internet-signup");
		mimeMap.put("iii","application/x-iphone");
		mimeMap.put("js","application/x-javascript");
		mimeMap.put("latex","application/x-latex");
		mimeMap.put("mdb","application/x-msaccess");
		mimeMap.put("crd","application/x-mscardfile");
		mimeMap.put("clp","application/x-msclip");
		mimeMap.put("dll","application/x-msdownload");
		mimeMap.put("m13","application/x-msmediaview");
		mimeMap.put("m14","application/x-msmediaview");
		mimeMap.put("mvb","application/x-msmediaview");
		mimeMap.put("wmf","application/x-msmetafile");
		mimeMap.put("mny","application/x-msmoney");
		mimeMap.put("pub","application/x-mspublisher");
		mimeMap.put("scd","application/x-msschedule");
		mimeMap.put("trm","application/x-msterminal");
		mimeMap.put("wri","application/x-mswrite");
		mimeMap.put("cdf","application/x-netcdf");
		mimeMap.put("nc","application/x-netcdf");
		mimeMap.put("pma","application/x-perfmon");
		mimeMap.put("pmc","application/x-perfmon");
		mimeMap.put("pml","application/x-perfmon");
		mimeMap.put("pmr","application/x-perfmon");
		mimeMap.put("pmw","application/x-perfmon");
		mimeMap.put("p12","application/x-pkcs12");
		mimeMap.put("pfx","application/x-pkcs12");
		mimeMap.put("p7b","application/x-pkcs7-certificates");
		mimeMap.put("spc","application/x-pkcs7-certificates");
		mimeMap.put("p7r","application/x-pkcs7-certreqresp");
		mimeMap.put("p7c","application/x-pkcs7-mime");
		mimeMap.put("p7m","application/x-pkcs7-mime");
		mimeMap.put("p7s","application/x-pkcs7-signature");
		mimeMap.put("sh","application/x-sh");
		mimeMap.put("shar","application/x-shar");
		mimeMap.put("swf","application/x-shockwave-flash");
		mimeMap.put("sit","application/x-stuffit");
		mimeMap.put("sv4cpio","application/x-sv4cpio");
		mimeMap.put("sv4crc","application/x-sv4crc");
		mimeMap.put("tar","application/x-tar");
		mimeMap.put("tcl","application/x-tcl");
		mimeMap.put("tex","application/x-tex");
		mimeMap.put("texi","application/x-texinfo");
		mimeMap.put("texinfo","application/x-texinfo");
		mimeMap.put("roff","application/x-troff");
		mimeMap.put("t","application/x-troff");
		mimeMap.put("tr","application/x-troff");
		mimeMap.put("man","application/x-troff-man");
		mimeMap.put("me","application/x-troff-me");
		mimeMap.put("ms","application/x-troff-ms");
		mimeMap.put("ustar","application/x-ustar");
		mimeMap.put("src","application/x-wais-source");
		mimeMap.put("cer","application/x-x509-ca-cert");
		mimeMap.put("crt","application/x-x509-ca-cert");
		mimeMap.put("der","application/x-x509-ca-cert");
		mimeMap.put("pko","application/ynd.ms-pkipko");
		mimeMap.put("zip","application/zip");
		mimeMap.put("au","audio/basic");
		mimeMap.put("snd","audio/basic");
		mimeMap.put("mid","audio/mid");
		mimeMap.put("rmi","audio/mid");
		mimeMap.put("mp3","audio/mpeg");
		mimeMap.put("aif","audio/x-aiff");
		mimeMap.put("aifc","audio/x-aiff");
		mimeMap.put("aiff","audio/x-aiff");
		mimeMap.put("m3u","audio/x-mpegurl");
		mimeMap.put("ra","audio/x-pn-realaudio");
		mimeMap.put("ram","audio/x-pn-realaudio");
		mimeMap.put("wav","audio/x-wav");
		mimeMap.put("bmp","image/bmp");
		mimeMap.put("cod","image/cis-cod");
		mimeMap.put("gif","image/gif");
		mimeMap.put("ief","image/ief");
		mimeMap.put("jpe","image/jpeg");
		mimeMap.put("jpeg","image/jpeg");
		mimeMap.put("jpg","image/jpeg");
		mimeMap.put("jfif","image/pipeg");
		mimeMap.put("svg","image/svg+xml");
		mimeMap.put("tif","image/tiff");
		mimeMap.put("tiff","image/tiff");
		mimeMap.put("ras","image/x-cmu-raster");
		mimeMap.put("cmx","image/x-cmx");
		mimeMap.put("ico","image/x-icon");
		mimeMap.put("pnm","image/x-portable-anymap");
		mimeMap.put("pbm","image/x-portable-bitmap");
		mimeMap.put("pgm","image/x-portable-graymap");
		mimeMap.put("ppm","image/x-portable-pixmap");
		mimeMap.put("rgb","image/x-rgb");
		mimeMap.put("xbm","image/x-xbitmap");
		mimeMap.put("xpm","image/x-xpixmap");
		mimeMap.put("xwd","image/x-xwindowdump");
		mimeMap.put("mht","message/rfc822");
		mimeMap.put("mhtml","message/rfc822");
		mimeMap.put("nws","message/rfc822");
		mimeMap.put("css","text/css http://www.dreamdu.com");
		mimeMap.put("323","text/h323");
		mimeMap.put("htm","text/html");
		mimeMap.put("html","text/html");
		mimeMap.put("stm","text/html");
		mimeMap.put("uls","text/iuls");
		mimeMap.put("bas","text/plain");
		mimeMap.put("c","text/plain");
		mimeMap.put("h","text/plain");
		mimeMap.put("txt","text/plain");
		mimeMap.put("rtx","text/richtext");
		mimeMap.put("sct","text/scriptlet");
		mimeMap.put("tsv","text/tab-separated-values");
		mimeMap.put("htt","text/webviewhtml");
		mimeMap.put("htc","text/x-component");
		mimeMap.put("etx","text/x-setext");
		mimeMap.put("vcf","text/x-vcard");
		mimeMap.put("mp2","video/mpeg");
		mimeMap.put("mpa","video/mpeg");
		mimeMap.put("mpe","video/mpeg");
		mimeMap.put("mpeg","video/mpeg");
		mimeMap.put("mpg","video/mpeg");
		mimeMap.put("mpv2","video/mpeg");
		mimeMap.put("mov","video/quicktime");
		mimeMap.put("qt","video/quicktime");
		mimeMap.put("lsf","video/x-la-asf");
		mimeMap.put("lsx","video/x-la-asf");
		mimeMap.put("asf","video/x-ms-asf");
		mimeMap.put("asr","video/x-ms-asf");
		mimeMap.put("asx","video/x-ms-asf");
		mimeMap.put("avi","video/x-msvideo");
		mimeMap.put("movie","video/x-sgi-movie");
		mimeMap.put("flr","x-world/x-vrml");
		mimeMap.put("vrml","x-world/x-vrml");
		mimeMap.put("wrl","x-world/x-vrml");
		mimeMap.put("wrz","x-world/x-vrml");
		mimeMap.put("xaf","x-world/x-vrml");
		mimeMap.put("xof","x-world/x-vrml");
		mimeMap.put("png","image/png");
		mimeMap.put("plist","application/xml");
		mimeMap.put("xml","application/xml");
		mimeMap.put("m3u8","application/octet-stream");
		mimeMap.put("ts","application/octet-stream");
		
		inlineMap.put("png", true);
		inlineMap.put("gif", true);
		inlineMap.put("jpg", true);
		inlineMap.put("jpeg", true);
		inlineMap.put("bmp", true);
		inlineMap.put("tif", true);
		inlineMap.put("tiff", true);
		inlineMap.put("swf", true);
		inlineMap.put("mp4", true);
		inlineMap.put("f4v", true);
		inlineMap.put("plist", true);
		inlineMap.put("m3u8", true);
		inlineMap.put("ts", true);
		
	}
	
	public static String getMineType(String sufffix){
		String mimeType=mimeMap.get(sufffix.toLowerCase());
		if(mimeType==null){
			return "application/octet-stream"; 
		}
		else{
			return mimeType;
		}
	}
	
	public static boolean isInlineType(String sufffix){
		Boolean inlineType=inlineMap.get(sufffix.toLowerCase());
		if(inlineType!=null){
			return true;
		}
		else{
			return false;
		}
	}
	
	
}
