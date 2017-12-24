
package com.xd.aselab.chinabank_shop.util;



import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

/**
 * 存储用户基本信息,包括账号、密码、用户名等
 * @author ??

 *
 */

public class SharePreferenceUtil {
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	public SharePreferenceUtil(Context context, String file) {
		sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
		editor = sp.edit();
	}
	// 用户的账号
	public void setAccount(String account)
	{
		editor.putString("account", account);
		editor.commit();
	}

	public void setPhotoUrl(String url) {
		editor.putString("photo", url);
		editor.commit();
	}
	public String getPhotoUrl() {
		return sp.getString("photo", "");
	}

	public void setHead_image(String head){
		editor.putString("head_image",head);
		editor.commit();
	}

	public String getHead_image(){
		return sp.getString("head_image","");
	}
	public void setShopTel(String Tel)
	{
		editor.putString("shopTele", Tel);
		editor.commit();
	}

	public String getShopTel()
	{
		return sp.getString("shopTele", "");
	}

	public void setCompany(String company)
	{
		editor.putString("company", company);
		editor.commit();
	}

	public String getCompany()
	{
		return sp.getString("company", "");
	}

	public void setManagerName(String name)
	{
		editor.putString("managerName", name);
		editor.commit();
	}

	public String getManagerName()
	{
		return sp.getString("managerName", "");
	}

	public String getAccount()
	{
		return sp.getString("account", "");
	}
    //用户以手机号当做登录账号
	public void setTelAccount(String telAccount)
	{
		editor.putString("telAccount", telAccount);
		editor.commit();
	}
	public String getTelAccount()
	{
		return sp.getString("telAccount", "");
	}
	
	// 用户的密码
	public void setPassword(String password) {
		editor.putString("password", password);
		editor.commit();
	}
	public String getPassword() {
		return sp.getString("password", "");
	}

	
	// 用户的Cookie
	public String getCookie() {
			return sp.getString("cookie", "");
	}
	public void setCookie(String cookie) {
			editor.putString("cookie", cookie);
			editor.commit();
	}
		
	// 用户的名字
	public String getUserName() {
		return sp.getString("username", "");
	}
	public void setUserName(String name) {
		editor.putString("username", name);
		editor.commit();
	}

	//店铺电话
	public String getShopMoblie() {
		return sp.getString("shopMoblie", "");
	}
	public void setShopMoblie(String mobile) {
		editor.putString("shopMoblie", mobile);
		editor.commit();
	}

	//店铺名称
	public String getShopName() {
		return sp.getString("shopName", "");
	}
	public void setShopName(String shopname) {
		editor.putString("shopName", shopname);
		editor.commit();
	}

	//店铺具体位置
	public String getShopLocationDescribe() {
		return sp.getString("ShopLocationDescribe", "");
	}
	public void setShopLocationDescribe(String location) {
		editor.putString("ShopLocationDescribe", location);
		editor.commit();
	}

	//店铺服务描述
	public String getShopSerDescribe() {
		return sp.getString("ShopServiceDescribe", "");
	}
	public void setShopSerDescribe(String service) {
		editor.putString("ShopServiceDescribe", service);
		editor.commit();
	}

	//店铺主人
	public String getShopOwnerName() {
		return sp.getString("OwnerName", "");
	}
	public void setShopOwnerName(String ownerName) {
		editor.putString("OwnerName", ownerName);
		editor.commit();
	}

	//店铺所在省
	public String getShopProvince() {
		return sp.getString("province", "");
	}
	public void setShopProvince(String province) {
		editor.putString("province", province);
		editor.commit();
	}

	//店铺所在市
	public String getShopCity() {
		return sp.getString("city", "");
	}
	public void setShopCity(String city) {
		editor.putString("city", city);
		editor.commit();
	}



	public String getWork_place() {
		return sp.getString("work_place", "");
	}
	public void setWork_place(String place) {
		editor.putString("work_place", place);
		editor.commit();
	}



	//店铺所在区
	public String getShopCounty() {
		return sp.getString("county", "");
	}
	public void setShopCounty(String county) {
		editor.putString("county", county);
		editor.commit();
	}

	//店铺所在街道
	public String getShopStreet() {
		return sp.getString("street", "");
	}
	public void setShopStreet(String street) {
		editor.putString("street", street);
		editor.commit();
	}

	//店铺类型
	public String getShopIndustryType() {
		return sp.getString("industryType", "");
	}
	public void setShopIndustryType(String industrytype) {
		editor.putString("industryType", industrytype);
		editor.commit();
	}

	//店铺联系人电话（经理电话）
	public String getShopManagerTel() {
		return sp.getString("managerTel", "");
	}
	public void setShopManagerTel(String managertel) {
		editor.putString("managerTel", managertel);
		editor.commit();
	}

	//经理工号
	public String getShopManagerAccount() {
		return sp.getString("managerAccount", "");
	}
	public void setShopManagerAccount(String manageraccount) {
		editor.putString("managerAccount", manageraccount);
		editor.commit();
	}

	/******店员*******/
	//店员名字
	public String getWorkerName() {
		return sp.getString("workerName", "");
	}
	public void setWorkerName(String workername) {
		editor.putString("workerName", workername);
		editor.commit();
	}

	//店员电话
	public String getWorkerTel() {
		return sp.getString("workerTel", "");
	}
	public void setWorkerTel(String workertel) {
		editor.putString("workerTel", workertel);
		editor.commit();
	}

	//店铺账号
	public String getShopAccount() {
		return sp.getString("shopAccount", "");
	}
	public void setShopAccount(String shopaccount) {
		editor.putString("shopAccount",shopaccount);
		editor.commit();
	}
	//用户类型
	public String getUserType() {
		return sp.getString("userType", "");
	}
	public void setUserType(String usertype) {
		editor.putString("userType",usertype);
		editor.commit();
	}

	//用户cookie
	public String getUserCookie() {
		return sp.getString("userCookie", "");
	}
	public void setUserCookie(String usercookie) {
		editor.putString("userCookie",usercookie);
		editor.commit();
	}


	/**
	 * 
	 ***********************************************
	 * */
	public void sharepreferenceClear(){
		editor.remove("userid");
		editor.remove("username");
		editor.remove("Moblie");
		editor.remove("Address");
		editor.clear();
		//editor.remove("password");
		editor.commit();
		System.out.println("all informmation cleared!");
	}
	// 是否第一次运行本应用
	public void setIsFirst(boolean isFirst) {
		editor.putBoolean("isFirst", isFirst);
		editor.commit();
	}

	public boolean getisFirst() {
		return sp.getBoolean("isFirst", true);
	}
	

	//是否登录成功
	public boolean getLoginSuccess()
	{
		   return sp.getBoolean("loginSuccess", false);
		   //editor.putString("loginSuccess", account);
		   
	}
	public void setLoginSuccess(boolean flag)
	{
		   editor.putBoolean("loginSuccess",flag);
		   editor.commit();
	}
	public String getStatus(){
		return sp.getString("status", "");
	}
	public void setStatus(String status){
		editor.putString("status", status);
		editor.commit();
	}

	public void setCardDivManagerImage(String head) {
		editor.putString("Carddiv_headimage",head);
		editor.commit();
	}
	public String getCardDivManagerImage() {
		return sp.getString("Carddiv_headimage","");
	}

	public void setGroup_Head_image(String group_Head_image) {
		editor.putString("group_Head_image",group_Head_image);
		editor.commit();
	}
	public String getGroup_Head_image() {
		return sp.getString("group_Head_image","");
	}

	//聊天参数
	public void setChatExtra(String chatExtra) {
		editor.putString("chatExtra", chatExtra);
		editor.commit();
	}
	public String getChatExtra() {
		return sp.getString("chatExtra", "");
	}

	//是否登录
	public void setIsLogin(boolean isLogin) {
		editor.putBoolean("isLogin", isLogin);
		editor.commit();
	}

	public boolean getisLogin() {
		return sp.getBoolean("isLogin", false);
	}
}
