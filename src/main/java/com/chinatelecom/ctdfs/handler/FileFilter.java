package com.chinatelecom.ctdfs.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.chinatelecom.ctdfs.convertor.IConvertor;
import com.chinatelecom.ctdfs.uploader.IUploader;
import com.chinatelecom.ctdfs.util.BodyReaderHttpServletRequestWrapper;
import com.chinatelecom.ctdfs.util.HttpHelper;
import com.chinatelecom.ctdfs.util.JSONUtil;
import com.chinatelecom.ctdfs.util.RetCode;
import com.chinatelecom.ctdfs.validation.IValidator;
import com.chinatelecom.udp.core.cmclient.CMClient;
import com.chinatelecom.udp.core.cmclient.ITypeValue;
import com.chinatelecom.udp.core.datarouter.io.DataOutputFormat;
import com.chinatelecom.udp.core.lang.json.JSONArray;
import com.chinatelecom.udp.core.lang.json.JSONObject;
import com.chinatelecom.udp.core.lang.text.TextHelper;

public class FileFilter implements Filter{
	private final static Logger logger = LogManager.getLogger(FileFilterHandler.class);
	private CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
	
	public void init(FilterConfig filterConfig) throws ServletException {
		try {
			String userName = null;
			ITypeValue userNameConfig = CMClient.getInstance().getValue(FileFilter.class.getName(),
					"hadoopusername");
			if (userNameConfig != null) {
				userName = userNameConfig.getStringValue();
				if (userName != null && userName.length() > 0) {
					System.setProperty("HADOOP_USER_NAME", userName);
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		logger.info("system has initialized");
	}

	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest sRequest, ServletResponse sResponse, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) sRequest;
		HttpServletResponse response = (HttpServletResponse) sResponse;
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
		response.setHeader("Access-Control-Allow-Headers", "X-PINGOTHER");
		response.setHeader("Access-Control-Max-Age", "1728000");
		response.setCharacterEncoding("UTF-8");  
		response.setContentType("application/json; charset=utf-8");
//		String ipAddress = WebHelper.getIPAddress(request);
//		ThreadContext.put("IP", ipAddress);
		String method = request.getMethod();
		String reference = request.getHeader("Referer");
		if ("OPTIONS".equals(method)) {
			//logger.info(String.format("request from %s,method=OPTIONS,reference=%s", ipAddress, reference));
		} else {
			//ServletRequest requestWrapper = new BodyReaderHttpServletRequestWrapper(request);  
			String requestName = TextHelper.getRightPart(request.getRequestURI(), "/", false);
			//String body = HttpHelper.getBodyString(requestWrapper); 
			//System.out.println(body);
			/*String param = "";
			 * 
			try {
				param = URLDecoder.decode(body,"utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} */  
			if(ServletFileUpload.isMultipartContent(request)) {
				MultipartHttpServletRequest multiReq = multipartResolver.resolveMultipart(request);
				request = multiReq;
			}
			JSONObject config = JSONUtil.readJSONFile("stategy.json");
			if (config.getJSONObject(requestName) != null) {
				JSONObject partConfig = new JSONObject();
				List<IValidator> validators = null;
				IUploader uploader = null;
				IConvertor convertor = null;
				partConfig = config.getJSONObject(requestName);
				if (partConfig.getJSONObject("validator") != null) {
					JSONArray validatorTypes = partConfig.getJSONObject("validator").getJSONArray("type");
					try {
						validators = new ArrayList<IValidator>();
						for (int i = 0; i < validatorTypes.length(); i++) {
							validators.add(DataAccess.createValidator(validatorTypes.getString(i)));
						}
					} catch (InstantiationException | IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Boolean result = false;
				for (IValidator validator : validators) {
					result = result | validator.validate(request,response);
				}
				if(validators.isEmpty() == true || result == true) {
					System.out.println("chain continue");
					chain.doFilter(request, response);
				} else {
					if(!response.containsHeader("message")) {
						PrintWriter out = null;
						try{
							JSONObject resultJson = new JSONObject();
							resultJson.put(RetCode.RESULT_KEY, RetCode.ERROR_CODE_21);
							resultJson.put(RetCode.RESULT_VALUE,RetCode.ERROR_DESC_21);
						    out = response.getWriter();
						    out.append(resultJson.toString());
						}
						catch (Exception e){
						    e.printStackTrace();
						    response.sendError(500);
						}
					}
				}
				/*if (validators.isEmpty() == true || result == true) {
					if (partConfig.getJSONObject("uploader") != null) {
						String uploaderType = partConfig.getString("uploader");
						System.out.println(uploaderType);
						try {
							uploader = DataAccess.createUploader(uploaderType);
						} catch (InstantiationException | IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if (partConfig.getJSONObject("convertor") != null) {
						String convertorType = partConfig.getString("convertor");
						try {
							convertor = DataAccess.createConvertor(convertorType);
						} catch (InstantiationException | IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}*/
			} else {
				chain.doFilter(sRequest, sResponse);
			}
		}
		
	}

}
