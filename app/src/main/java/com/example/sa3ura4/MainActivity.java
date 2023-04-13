package com.example.sa3ura4;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import cn.com.newland.nle_sdk.requestEntity.SignIn;
import cn.com.newland.nle_sdk.responseEntity.SensorInfo;
import cn.com.newland.nle_sdk.responseEntity.User;
import cn.com.newland.nle_sdk.responseEntity.base.BaseResponseEntity;
import cn.com.newland.nle_sdk.util.NCallBack;
import cn.com.newland.nle_sdk.util.NetWorkBusiness;
import retrofit2.Call;

public class MainActivity extends AppCompatActivity {

    private EditText user,pwd,Temp,Humi,ID;
    private Button login,getTemp_Humi,light_ON,light_OFF;
    private String accessToken,_user,_pwd;
    private NetWorkBusiness netWorkBusiness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = findViewById(R.id.username);
        pwd = findViewById(R.id.password);
        Temp = findViewById(R.id.Temp);
        Humi = findViewById(R.id.Humi);
        ID = findViewById(R.id.ID);
        login = findViewById(R.id.login);
        getTemp_Humi = findViewById(R.id.getTemp_Humi);
        light_ON = findViewById(R.id.light_ON);
        light_OFF = findViewById(R.id.light_OFF);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _user = user.getText().toString();
                _pwd = pwd.getText().toString();
                if (_user.equals("") || _pwd.equals("")){
                    Toast.makeText(MainActivity.this, "用户名或密码不能为空!", Toast.LENGTH_SHORT).show();
                }else {
                    login();
                }
            }
        });
        light_ON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ID.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this, "请输入设备ID", Toast.LENGTH_SHORT).show();
                }else {
                    control(ID.getText().toString(),"light",1);
                }
            }
        });
        light_OFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ID.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this, "请输入设备ID", Toast.LENGTH_SHORT).show();
                }else {
                    control(ID.getText().toString(),"light",0);
                }
            }
        });
        getTemp_Humi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ID.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this, "请输入设备ID", Toast.LENGTH_SHORT).show();
                }else {
                    getTempHumi(ID.getText().toString(),"Temp,Humi");
                }
            }
        });
    }
    public void login(){
        netWorkBusiness = new NetWorkBusiness("","https://api.nlecloud.com");
        netWorkBusiness.signIn(new SignIn(_user, _pwd), new NCallBack<BaseResponseEntity<User>>(getApplicationContext()) {
            @Override
            protected void onResponse(BaseResponseEntity<User> userBaseResponseEntity) {
                if (userBaseResponseEntity.getStatus() == 0){
                    accessToken = userBaseResponseEntity.getResultObj().getAccessToken();
                    Toast.makeText(MainActivity.this, "Login Success.", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this, userBaseResponseEntity.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponseEntity<User>> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void control(String ID,String name,int zhi){
        netWorkBusiness = new NetWorkBusiness(accessToken,"https://api.nlecloud.com");
        netWorkBusiness.control(ID, name, zhi, new NCallBack<BaseResponseEntity>(getApplicationContext()) {
            @Override
            protected void onResponse(BaseResponseEntity baseResponseEntity) {
                if (baseResponseEntity.getStatus() == 0){
                    if (name == "light" && zhi == 1){
                        Toast.makeText(MainActivity.this, "灯光已开启。", Toast.LENGTH_SHORT).show();
                    }else if (name == "light" && zhi == 0){
                        Toast.makeText(MainActivity.this, "灯光已关闭", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(MainActivity.this, baseResponseEntity.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void getTempHumi(String ID,String name){
        netWorkBusiness = new NetWorkBusiness(accessToken,"https://api.nlecloud.com");
        netWorkBusiness.getSensors(ID, name, new NCallBack<BaseResponseEntity<List<SensorInfo>>>(getApplicationContext()) {
            @Override
            protected void onResponse(BaseResponseEntity<List<SensorInfo>> listBaseResponseEntity) {
                if (listBaseResponseEntity.getStatus() == 0){
                    List<SensorInfo> obj = listBaseResponseEntity.getResultObj();
                    for (SensorInfo abc : obj){
                        if (abc.getApiTag().equals("Temp")){
                            String getTemp = abc.getValue();
                            Toast.makeText(MainActivity.this, "温度获取成功！", Toast.LENGTH_SHORT).show();
                            Temp.setText(getTemp+"℃");
                        }if (abc.getApiTag().equals("Humi")){
                            String getHumi = abc.getValue();
                            Toast.makeText(MainActivity.this, "湿度获取成功！", Toast.LENGTH_SHORT).show();
                            Humi.setText(getHumi+"%RH");
                        }
                    }
                }else {
                    Toast.makeText(MainActivity.this, listBaseResponseEntity.getMsg(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}