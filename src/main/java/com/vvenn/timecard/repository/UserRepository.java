package com.vvenn.timecard.repository;

import java.util.List;

import com.vvenn.timecard.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * JpaRepository というテーブル操作を定義したインターフェースを継承した、<br>
 * 「users」テーブルの操作を行うリポジトリのインターフェースです。
 * 
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * usersテーブルから引数とusernameが一致するデータを取得します。
     * 
     * @param username メールアドレス
     * @return 一致するユーザー
     */
    public User findByUsername(String username);

    /**
     * アクティブなユーザー一覧を取得します。
     * 
     * @return アクティブなユーザーリスト
     */
    @Query(value = "SELECT * FROM users WHERE inactive_flag = false", nativeQuery = true)
    public List<User> findActiveUserAll();

    /**
     * アクティブなユーザー一覧ページを取得します。
     * 
     * @param pageable ページャ
     * @return Pager でラップされた User の検索結果
     */
    @Query(value = "SELECT * FROM users WHERE inactive_flag = false ORDER BY joined_at ASC", nativeQuery = true)
    public Page<User> findActiveUserAll(Pageable pageable);
}