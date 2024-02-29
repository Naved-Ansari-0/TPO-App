package in.navedansari.tpoapp.models;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("email")
    private String email;
    @SerializedName("pwd")
    private String pwd;
    @SerializedName("newpwd")
    private String newpwd;

    public String getNewpwd() {
        return newpwd;
    }

    public void setNewpwd(String newpwd) {
        this.newpwd = newpwd;
    }

    public LoginRequest(String email, String pwd) {
        this.email = email;
        this.pwd = pwd;
    }
    public LoginRequest(String email, String pwd, String newpwd) {
        this.email = email;
        this.pwd = pwd;
        this.newpwd = newpwd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
