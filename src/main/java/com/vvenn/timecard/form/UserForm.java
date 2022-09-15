package com.vvenn.timecard.form;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ユーザー詳細画面フォームの入力値をチェックするmodel
 */
@Data
@NoArgsConstructor
public class UserForm {

    private Long id;

    @NotBlank(message = "メールアドレスを入力してください。")
    @Email(message = "メールアドレスを正しく入力してください。")
    private String username;

    @Size(min = 8, max = 20, message = "パスワードを8~20文字で入力してください。", groups = { UserFormAction.Password.class,
            UserFormAction.Create.class })
    private String password;

    private String confirmPassword;

    @Size(min = 1, max = 40, message = "氏名を40文字以内で入力してください。")
    private String displayName;

    private String sectionCode;

    @Pattern(regexp = "^([2-9][0-9]{3})-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$", message = "日付が指定可能な日付の範囲外です。")
    private String joinedAt;

    private Boolean adminFlag;

    @AssertTrue(message = "パスワードが一致しません。再度入力してください。", groups = { UserFormAction.Password.class,
            UserFormAction.Create.class, UserFormAction.Update.class })
    public boolean isMatchPassword() {
        return (this.password == null) || (this.password.equals("")) ? true
                : (this.confirmPassword == null) || (this.confirmPassword.equals("")) ? false
                        : (this.confirmPassword.equals(this.password)) ? true : false;
    }

    @AssertTrue(message = "パスワードを8~20文字で入力してください。", groups = { UserFormAction.Update.class })
    public boolean isCheckPassword() {
        boolean result = false;
        final int min = 8;
        final int max = 20;
        if ((this.password == null) || (this.password.equals(""))
                || (this.password.length() >= min && this.password.length() <= max)) {
            result = true;
        }

        return result;
    }

}