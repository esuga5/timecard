package com.vvenn.timecard.form;

import javax.validation.groups.Default;

/**
 * UserForm内バリデーションのgroups化に使用するインターフェース
 */
public interface UserFormAction {
    interface Create extends Default {
    }

    interface Update extends Default {
    }

    interface Password {
    }
}