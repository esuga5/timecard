package com.vvenn.timecard.form;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 非同期通信の成否結果を保持するJsonエンティティ
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseJson implements Serializable {

    private static final long serialVersionUID = 1L;

    boolean resultFlag;
    String message;

}