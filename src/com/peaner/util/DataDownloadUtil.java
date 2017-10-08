package com.peaner.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * 数据抓取核心类
 * @author PEANER-Li
 *
 */
class DataDownloadUtil {
	
	//第一步 获取源数据
	//第二步 解析源代码
	//第三步 数据筛选
	//第四步 数据存储
	
	/**
	 * 获取网页源数据
	 * @param url 网址
	 * @param encoding 编码
	 * @return
	 */
	public static String getHtmlResourceByurl(String url,String encoding){
		InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;
		StringBuffer sbBuffer = new StringBuffer();
		try {
			//建立网络连接
			URL urlObj = new URL(url);
			//打开网络连接
			URLConnection urlConnection = urlObj.openConnection();
			//读取源代码
			inputStreamReader = new InputStreamReader(urlConnection.getInputStream(),encoding);
			//高效率读取数据
			reader = new BufferedReader(inputStreamReader);
			//建立临时字符
			String temp = "";
			while((temp = reader.readLine())!= null){
				sbBuffer.append(temp+"\n");
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(reader!=null){
					reader.close();
				}
				if(inputStreamReader!=null){
					inputStreamReader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sbBuffer.toString();
	}
	
	
	/**
	 * 数据筛选
	 * @param url
	 * @param encoding
	 * @return
	 */
	public static List<HashMap<String, String>> getHotelInfo(String url,String encoding){
		//获取携程网页源码
		String htmlResource = getHtmlResourceByurl(url, encoding);
		//解析携程网页源码
		Document document = Jsoup.parse(htmlResource);
		//获取酒店相关节点
		Element element = document.getElementById("hotel_list");
		//获取酒店列表信息
		Elements elements = document.getElementsByClass("hotel_new_list");
		
		Elements elements2 = document.getElementsByClass("c_page_list layoutfix");
		HashMap<String, String> map = null;
		List<HashMap<String, String>> lists = new ArrayList<>();
		//遍历酒店列表信息
		for(Element myelement : elements){
			map = new HashMap<String, String>();
			//获取酒店图片
			String imgSrc = myelement.getElementsByTag("img").attr("src");
			//获取酒店名称
			String title = myelement.getElementsByTag("img").attr("alt");
			//获取酒店描述
			//String des = myelement.getElementsByTag("searchresult_htladdress")
			String desc = document.getElementsByClass("hotel_item_htladdress").text();
			map.put("imgSrc",imgSrc);
			map.put("title",title);
			map.put("desc", desc);
			lists.add(map);
		}
		
		for(Element myelement1 : elements2){
			map = new HashMap<String, String>();
			String nextPage = myelement1.getElementsByClass("a").attr("href");
			map.put("nextPage", nextPage);
			lists.add(map);
		}
		
		return lists;
	}
	
	
	public static void main(String[] args) {
		boolean flag = false;
		while(!flag){
			List<HashMap<String, String>> lists = getHotelInfo("http://hotels.ctrip.com/hotel/shanghai2#ctm_ref=hod_hp_sb_lst", "utf-8");
			
			for(HashMap<String, String> hashMap : lists){
				System.out.println("酒店图片"+hashMap.get("imgSrc"));
				System.out.println("酒店名称"+hashMap.get("title"));
				System.out.println("酒店描述"+hashMap.get("desc"));
				lists = getHotelInfo(hashMap.get("nextPage"), "utf-8");
			}
		}
		
	}
}
