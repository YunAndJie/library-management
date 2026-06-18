package com.pwenjie.controller;

import com.pwenjie.aop.RateLimit;
import com.pwenjie.common.result.Result;
import com.pwenjie.dto.response.CategroyVO;
import com.pwenjie.service.CategroyService;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/categories")
@Validated
public class CategoryController {

    @Autowired
    private CategroyService categroyService;

    @PostMapping
    @RateLimit(maxRequests = 5, windowSeconds = 60)
    public Result<CategroyVO> addCategory(@RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        String description = (String) body.get("description");
        Long parentId = body.get("parentId") != null ? ((Number) body.get("parentId")).longValue() : null;
        Integer sort = body.get("sort") != null ? ((Number) body.get("sort")).intValue() : null;

        CategroyVO vo = categroyService.addCategory(name, description, parentId, sort);
        return Result.success(vo, "添加分类成功");
    }

    @GetMapping("/{id}")
    public Result<CategroyVO> getCategoryById(@PathVariable @Min(1) Long id) {
        CategroyVO vo = categroyService.getCategoryById(id);
        return Result.success(vo);
    }

    @GetMapping
    public Result<List<CategroyVO>> getAllCategories() {
        List<CategroyVO> vos = categroyService.getAllCategories();
        return Result.success(vos);
    }

    @GetMapping("/parent/{parentId}")
    public Result<List<CategroyVO>> getSubCategories(@PathVariable @Min(0) Long parentId) {
        List<CategroyVO> vos = categroyService.getSubCategories(parentId);
        return Result.success(vos);
    }

    @PutMapping("/{id}")
    @RateLimit(maxRequests = 5, windowSeconds = 60)
    public Result<CategroyVO> updateCategory(@PathVariable @Min(1) Long id, @RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        String description = (String) body.get("description");
        Long parentId = body.get("parentId") != null ? ((Number) body.get("parentId")).longValue() : null;
        Integer sort = body.get("sort") != null ? ((Number) body.get("sort")).intValue() : null;

        CategroyVO vo = categroyService.updateCategory(id, name, description, parentId, sort);
        return Result.success(vo, "更新分类成功");
    }

    @DeleteMapping("/{id}")
    @RateLimit(maxRequests = 5, windowSeconds = 60)
    public Result<Void> deleteCategory(@PathVariable @Min(1) Long id) {
        boolean success = categroyService.deleteCategory(id);
        if (success) {
            return Result.success(null, "删除成功");
        } else {
            return Result.error("删除失败");
        }
    }

    @GetMapping("/count")
    public Result<Integer> getCategoryCount() {
        Integer count = categroyService.getCategoryCount();
        return Result.success(count);
    }
}
