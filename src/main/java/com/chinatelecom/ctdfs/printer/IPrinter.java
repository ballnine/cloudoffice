package com.chinatelecom.ctdfs.printer;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.chinatelecom.ctdfs.handler.IFormFieldInfo;

public interface IPrinter {
	void print(HttpServletResponse response, List<IFormFieldInfo> webFields);
}
