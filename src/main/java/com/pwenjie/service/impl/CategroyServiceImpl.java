package com.pwenjie.service.impl;

import com.pwenjie.common.constant.CacheConstants;
import com.pwenjie.common.enums.ResponseCodeEnum;
import com.pwenjie.common.exception.BusinessException;
import com.pwenjie.dto.response.CategroyVO;
import com.pwenjie.entity.Categroy;
import com.pwenjie.mapper.CategroyMapper;
import com.pwenjie.service.CategroyService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CategroyServiceImpl implements CategroyService {

    @Autowired
    private CategroyMapper categroyMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public CategroyVO addCategory(String name, String description, Long parentId, Integer sort) {
        if (!StringUtils.hasText(name)) {
            throw new BusinessException(ResponseCodeEnum.BAD_REQUEST.getCode(), "分类名称不能为空");
        }

        Categroy exist = categroyMapper.selectByName(name);
        if (exist != null) {
            throw new BusinessException(ResponseCodeEnum.BAD_REQUEST.getCode(), "分类名称已存在");
        }

        if (sort == null) {
            sort = 0;
        }

        Categroy categroy = new Categroy();
        categroy.setName(name);
        categroy.setDescription(description);
        categroy.setParentId(parentId);
        categroy.setSort(sort);
        categroy.setCreateTime(new Date());

        int result = categroyMapper.insert(categroy);
        if (result <= 0) {
            throw new BusinessException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.getCode(), "添加分类失败");
        }

        clearCategoryListCache();
        return convertToVO(categroy);
    }

    @Override
    public CategroyVO getCategoryById(Long id) {
        String cacheKey = CacheConstants.CATEGORY_PREFIX + id;
        CategroyVO cached = (CategroyVO) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        Categroy categroy = categroyMapper.selectById(id);
        if (categroy == null) {
            throw new BusinessException(ResponseCodeEnum.NOT_FOUND);
        }

        CategroyVO vo = convertToVO(categroy);
        redisTemplate.opsForValue().set(cacheKey, vo, CacheConstants.CATEGORY_EXPIRE, TimeUnit.SECONDS);
        return vo;
    }

    @Override
    public List<CategroyVO> getAllCategories() {
        String cacheKey = CacheConstants.CATEGORY_LIST_PREFIX + "all";
        @SuppressWarnings("unchecked")
        List<CategroyVO> cached = (List<CategroyVO>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        List<Categroy> categories = categroyMapper.selectAll();
        List<CategroyVO> vos = categories.stream().map(this::convertToVO).collect(Collectors.toList());
        List<CategroyVO> tree = buildTree(vos);
        redisTemplate.opsForValue().set(cacheKey, tree, CacheConstants.CATEGORY_EXPIRE, TimeUnit.SECONDS);
        return tree;
    }

    @Override
    public List<CategroyVO> getSubCategories(Long parentId) {
        String cacheKey = CacheConstants.CATEGORY_LIST_PREFIX + "parent:" + parentId;
        @SuppressWarnings("unchecked")
        List<CategroyVO> cached = (List<CategroyVO>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        List<Categroy> categories = categroyMapper.selectByParentId(parentId);
        List<CategroyVO> vos = categories.stream().map(this::convertToVO).collect(Collectors.toList());
        redisTemplate.opsForValue().set(cacheKey, vos, CacheConstants.CATEGORY_EXPIRE, TimeUnit.SECONDS);
        return vos;
    }

    @Override
    public CategroyVO updateCategory(Long id, String name, String description, Long parentId, Integer sort) {
        Categroy categroy = categroyMapper.selectById(id);
        if (categroy == null) {
            throw new BusinessException(ResponseCodeEnum.NOT_FOUND);
        }

        if (StringUtils.hasText(name)) {
            Categroy exist = categroyMapper.selectByName(name);
            if (exist != null && !exist.getId().equals(id)) {
                throw new BusinessException(ResponseCodeEnum.BAD_REQUEST.getCode(), "分类名称已存在");
            }
            categroy.setName(name);
        }
        if (description != null) {
            categroy.setDescription(description);
        }
        if (parentId != null) {
            if (parentId.equals(id)) {
                throw new BusinessException(ResponseCodeEnum.BAD_REQUEST.getCode(), "父分类不能是自身");
            }
            categroy.setParentId(parentId);
        }
        if (sort != null) {
            categroy.setSort(sort);
        }

        int result = categroyMapper.update(categroy);
        if (result <= 0) {
            throw new BusinessException(ResponseCodeEnum.INTERNAL_SERVER_ERROR.getCode(), "更新分类失败");
        }

        redisTemplate.delete(CacheConstants.CATEGORY_PREFIX + id);
        clearCategoryListCache();
        return getCategoryById(id);
    }

    @Override
    public boolean deleteCategory(Long id) {
        Categroy categroy = categroyMapper.selectById(id);
        if (categroy == null) {
            throw new BusinessException(ResponseCodeEnum.NOT_FOUND);
        }

        int childCount = categroyMapper.countByParentId(id);
        if (childCount > 0) {
            throw new BusinessException(ResponseCodeEnum.BAD_REQUEST.getCode(), "该分类下存在子分类，无法删除");
        }

        int result = categroyMapper.deleteById(id);
        if (result > 0) {
            redisTemplate.delete(CacheConstants.CATEGORY_PREFIX + id);
            clearCategoryListCache();
            return true;
        }
        return false;
    }

    @Override
    public int getCategoryCount() {
        return categroyMapper.count();
    }

    private CategroyVO convertToVO(Categroy categroy) {
        if (categroy == null) return null;
        CategroyVO vo = new CategroyVO();
        BeanUtils.copyProperties(categroy, vo);
        return vo;
    }

    private List<CategroyVO> buildTree(List<CategroyVO> all) {
        return all.stream()
                .filter(c -> c.getParentId() == null || c.getParentId() == 0)
                .peek(c -> c.setChildren(getChildren(c, all)))
                .collect(Collectors.toList());
    }

    private List<CategroyVO> getChildren(CategroyVO parent, List<CategroyVO> all) {
        List<CategroyVO> children = all.stream()
                .filter(c -> parent.getId().equals(c.getParentId()))
                .peek(c -> c.setChildren(getChildren(c, all)))
                .collect(Collectors.toList());
        return children.isEmpty() ? null : children;
    }

    private void clearCategoryListCache() {
        var keys = redisTemplate.keys(CacheConstants.CATEGORY_LIST_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
