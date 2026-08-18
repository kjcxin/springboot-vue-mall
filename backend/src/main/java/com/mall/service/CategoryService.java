package com.mall.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.common.BusinessException;
import com.mall.entity.Category;
import com.mall.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /** 分类树 */
    public List<Category> tree() {
        List<Category> all = categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, 1).orderByAsc(Category::getSort));
        Map<Long, Category> map = all.stream().collect(Collectors.toMap(Category::getId, c -> c));
        List<Category> roots = new ArrayList<>();
        for (Category c : all) {
            if (c.getParentId() == null || c.getParentId() == 0L) {
                roots.add(c);
            } else {
                Category parent = map.get(c.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(c);
                }
            }
        }
        return roots;
    }

    /** 分类及其所有子孙分类 ID（用于商品按分类筛选时包含子分类） */
    public List<Long> categoryAndChildrenIds(Long categoryId) {
        List<Category> all = categoryMapper.selectList(new LambdaQueryWrapper<Category>().eq(Category::getStatus, 1));
        List<Long> ids = new ArrayList<>();
        collectIds(all, categoryId, ids);
        return ids;
    }

    private void collectIds(List<Category> all, Long parentId, List<Long> ids) {
        ids.add(parentId);
        for (Category c : all) {
            if (parentId.equals(c.getParentId())) {
                collectIds(all, c.getId(), ids);
            }
        }
    }

    // ===== 管理端 =====
    public List<Category> adminList() {
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>().orderByAsc(Category::getSort));
    }

    public void adminCreate(Category category) {
        if (category.getParentId() == null) {
            category.setParentId(0L);
        }
        if (category.getSort() == null) {
            category.setSort(0);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        categoryMapper.insert(category);
    }

    public void adminUpdate(Long id, Category category) {
        category.setId(id);
        categoryMapper.updateById(category);
    }

    public void adminDelete(Long id) {
        Long childCount = categoryMapper.selectCount(new LambdaQueryWrapper<Category>().eq(Category::getParentId, id));
        if (childCount != null && childCount > 0) {
            throw new BusinessException("存在子分类，无法删除");
        }
        categoryMapper.deleteById(id);
    }
}
