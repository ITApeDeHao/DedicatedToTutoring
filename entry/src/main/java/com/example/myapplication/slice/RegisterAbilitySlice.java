package com.example.myapplication.slice;

import com.example.myapplication.ResourceTable;
import com.example.myapplication.dialog.PromptDialog;
import com.example.myapplication.pojo.QueryParameter;
import com.example.myapplication.pojo.User;
import com.example.myapplication.utils.ApiUtil;
import com.example.myapplication.utils.LocateDataUtil;
import com.example.myapplication.utils.LogUtil;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.agp.components.TextField;
import ohos.app.Context;
import ohos.app.dispatcher.task.TaskPriority;
import ohos.utils.zson.ZSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegisterAbilitySlice extends AbilitySlice implements Component.ClickedListener {
    private TextField usernametext;
    private TextField passwordtext;
    private TextField againpasswordtext;
    private Button registerbutton;
    private String flag;
    private String id;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_register);
        usernametext = (TextField) findComponentById(ResourceTable.Id_RegisterUserName);
        passwordtext = (TextField) findComponentById(ResourceTable.Id_RegisterPassWord);
        againpasswordtext = (TextField) findComponentById(ResourceTable.Id_AgainPassWord);
        registerbutton = (Button) findComponentById(ResourceTable.Id_Register);
        registerbutton.setClickedListener(this);
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }

    @Override
    public void onClick(Component component) {
        if (component == registerbutton){
            String username = usernametext.getText();
            String password = passwordtext.getText();
            String againpassword = againpasswordtext.getText();
            if (username.length() < 5 || username.length() > 11){
                 /*
                 ??????????????????????????????
                  */
                PromptDialog.showDialog(this,"??????????????????6~11???!",PromptDialog.Danger);
                return;
            }
            if (password.equals("") || password.length() > 16 || password.length() < 6){
                /*
                 ?????????????????????????????????
                 */
                PromptDialog.showDialog(this,"???????????????6~16???!",PromptDialog.Danger);
                return;
            }
            if (againpassword.equals("") || !againpassword.equals(password)){
                /*
                 ????????????????????? ???????????????????????????  ????????????
                 */
                PromptDialog.showDialog(this,"???????????????????????????????????????!",PromptDialog.Danger);
                return;
            }
            User user = new User(username,password);
            Context context = this;
            getGlobalTaskDispatcher(TaskPriority.DEFAULT).asyncDispatch(()->{
                String url = "http://123.56.152.169:8111/register";
                HashMap<String,String> paramsMap=new HashMap<>();
                paramsMap.put("phone",user.getName());
                paramsMap.put("paswd",user.getPassword());
                paramsMap.put("icon","?????????");
                paramsMap.put("address","??????????????????");
                ZSONObject zsonObject = new ZSONObject();
                for (String key : paramsMap.keySet()) {
                    //??????????????????
                    zsonObject.put(key, paramsMap.get(key));
                }
                try {
                    LogUtil.info( "????????????" +  zsonObject.toString());
                    ZSONObject result = ApiUtil.api(url,ApiUtil.post,new QueryParameter(),zsonObject);
                    context.getUITaskDispatcher().asyncDispatch(()->{
                        try {
                            id = result.getZSONObject("data").getString("id");
                            flag = result.getString("message");
                        }catch (Exception e){
                            /*
                            *  ???????????? ToastDialog
                            * */
                            PromptDialog.showDialog(this,"??????????????????????????????????????????????????????!",PromptDialog.Danger);
                        }
                    });
                } catch (IOException e) {
                    PromptDialog.showDialog(this,"??????????????????????????????????????????????????????!",PromptDialog.Danger);
                }
            });
            if("????????????".equals(flag)){
                PromptDialog.showDialog(this,flag + "!",PromptDialog.Success);
                Map<String, String> map = new HashMap<>();
                map.put("ID",id);
                map.put("UserName",username);
                map.put("PassWord",password);
                LocateDataUtil.writeData(context,map);
                onBackPressed();
            }else {
                PromptDialog.showDialog(this,flag + "!",PromptDialog.promt);
            }
        }

    }
}
