package com.pwenjie.service;

import com.pwenjie.dto.response.CategroyVO;
import com.pwenjie.entity.Categroy;

import java.util.List;

public interface CategroyService {

    CategroyVO addCategory(String name, String description, Long parentId, Integer sort);

    CategroyVO getCategoryById(Long id);

    List<CategroyVO> getAllCategories();

    List<CategroyVO> getSubCategories(Long parentId);

    CategroyVO updateCategory(Long id, String name, String description, Long parentId, Integer sort);

    boolean deleteCategory(Long id);

    int getCategoryCount();
}
