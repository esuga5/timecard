package com.vvenn.timecard.service;

import java.util.List;

import com.vvenn.timecard.entity.Section;
import com.vvenn.timecard.repository.SectionRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

@Service
public class SectionService {

    @Autowired
    private SectionRepository repository;

    /**
     * sectionsテーブルからSectionレコード一覧を取得します。
     * 
     * @return Sectionリスト
     */
    public List<Section> getSectionList() {
        return this.repository.findAll();
    }
}
