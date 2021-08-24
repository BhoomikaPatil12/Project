package com.tea.ilearn.net.EduKG;

import android.os.Handler;

import com.tea.ilearn.net.APIRequest;

import java.util.HashMap;
/**
 * Network communication to EduKG
 */
public class EduKG extends APIRequest {
    private EduKG() {
        super(
                "http://open.edukg.cn/opedukg/api",
                "/typeAuth/user/login",
                "/typeOpen/open",
                new HashMap<String, Object>(){{
                    put("phone", "13717683859");
                    put("password", "abc123456");
                }},
                "id",
                "请先登录",
                LoginResponse.class
        );
    }
    private static EduKG instance = new EduKG();
    public static EduKG getInst() {
        return instance;
    }

    protected static int successCode = 0;

    @Override
    protected void onRefreshSuccess(Object response) {
        tokenValue = ((LoginResponse) response).id;
    }

    public void fuzzySearchEntity(String course, String searchKey, Handler handler) {
        // TODO: cancel request to save resources
        GET("/instanceList",
            new HashMap<String, Object>() {{
                put("course", course);
                put("searchKey", searchKey);
            }},
            p -> p.asEduKGResponseList(Entity.class),
            handler);
    }

    public void getEntityDetail(String course, String entityName, Handler handler) {
        GET("/infoByInstanceName",
            new HashMap<String, Object>() {{
                put("course", course);
                put("name", entityName);
            }},
            p -> p.asEduKGResponse(EntityDetail.class),
            handler);
    }

}