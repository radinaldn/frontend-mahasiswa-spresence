package com.inkubator.radinaldn.smartabsen.config;

/**
 * Created by radinaldn on 17/03/18.
 */

public class ServerConfig {
//    public static final String DOMAIN_SERVER = "http://192.168.43.104/";
//    public static final String DOMAIN_SERVER = "http://192.168.1.100/";
    public static final String DOMAIN_SERVER = "https://topapp.id/";
    public static final String SERVER_URL = DOMAIN_SERVER+"smart-presence/api/v1/";
    public static final String API_ENDPOINT = SERVER_URL;
    public static final String IMAGE_PATH = DOMAIN_SERVER+"smart-presence/web/files/images/";
    public static final String QRCODE_PATH = DOMAIN_SERVER+"smart-presence/web/files/qrcode/";
    public static final String UPLOAD_FOTO_ENDPOINT = DOMAIN_SERVER+"smart-presence/api/upload/upload-foto.php";
}
