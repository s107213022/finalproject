package org.iBeaconapp.beaconreference;

import android.os.Environment;
import android.os.StrictMode;
import android.widget.Toast;

import com.scottyab.aescrypt.AESCrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class FileIO {
    private static String exe = "ajnumber";
    public static void writeFile(String ajdata) {
        /**決定檔案名稱*/
        String fileName = "aj";

        String encrypted_aj_Msg = null;
        try{
            encrypted_aj_Msg = AESCrypt.encrypt(exe,ajdata);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        /**決定檔案被存放的路徑*/
//      "/storage/emulated/0/BlogExport"
        String absoluteFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ibeaconAppData";
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        boolean t = true;
        try {
            /**新增BlogExport的資料夾*/
            File file = new File(absoluteFilePath);
            if (file.mkdir()) {
                System.out.println("新增資料夾");
            } else {
                System.out.println("資料夾已存在");
            }
            /*檔案輸出-> /storage/emulated/0/BlogExport/碼農日常檔案輸出.txt*/
            File fileLocation =
                    new File(absoluteFilePath + "/" + fileName + ".txt");
            /**撰寫檔案內容*/
            FileOutputStream fos = new FileOutputStream(fileLocation);
            fos.write(encrypted_aj_Msg.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            //t = false;
        }
        //return t;
    }

    public static String readFile() {
        /**決定檔案名稱*/
        String fileName = "aj";
        int i = 0;
        char c;
        String result = null;
        String Decrypt = null;

        /**決定檔案被存放的路徑*/
//      "/storage/emulated/0/BlogExport"
        String absoluteFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ibeaconAppData";
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        try {
            /*檔案輸出-> /storage/emulated/0/BlogExport/碼農日常檔案輸出.txt*/
            File fileLocation =
                    new File(absoluteFilePath + "/" + fileName + ".txt");
            if(fileLocation.exists()){
                FileInputStream fos = new FileInputStream(fileLocation);
                /**撰寫檔案內容*/
                StringBuilder str = new StringBuilder();
                while((i=fos.read())!=-1)
                {
                    // converts integer to character
                    c=(char)i;
                    str.append(c);
                    // prints character
                    //System.out.print(c);
                }
                result = String.valueOf(str);
                fos.close();
                try {
                    Decrypt = AESCrypt.decrypt(exe,result);
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
            }else {
                return "1";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Decrypt;
    }
}
