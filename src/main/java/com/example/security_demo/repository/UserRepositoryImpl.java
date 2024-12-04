package com.example.security_demo.repository;

import com.example.security_demo.dto.request.user.UserSearchRequest;
import com.example.security_demo.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepositoryImpl implements UserRepositoryCustom{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<User> search(UserSearchRequest request) {
        return List.of();
    }

    @Override
    public Long count(UserSearchRequest request) {
        return 0L;
    }

//    @Override
//    public List<User> search(UserSearchRequest request) {
//        Map<String, Object> values = new HashMap<>();
//        String sql = "select e from UserEntity e " + createWhereQuery(request, values) + createOrderQuery(request.getSortBy());
//        Query query = entityManager.createQuery(sql, User.class);
//
//        // Set parameters
//        values.forEach(query::setParameter);
//
//        // Pagination
//        query.setFirstResult((request.getPageIndex() - 1) * request.getPageSize());
//        query.setMaxResults(request.getPageSize());
//
//        return query.getResultList();
//    }
//
//    @Override
//    public Long count(UserSearchRequest request) {
//        Map<String, Object> values = new HashMap<>();
//        String sql = "select count(e) from UserEntity e " + createWhereQuery(request, values);
//        Query query = entityManager.createQuery(sql, Long.class);
//        values.forEach(query::setParameter);
//        return (Long) query.getSingleResult();
//    }

//    private String createWhereQuery(UserSearchRequest request, Map<String, Object> values) {
//        StringBuilder sql = new StringBuilder();
//        sql.append(" left join RoleEntity r on (e.roleId = r.id) ");
//        sql.append(" where e.deleted = false");
//
//        if (StringUtils.isNotBlank(request.getKeyword())) {
//            sql.append(
//                    " and ( lower(e.username) like :keyword"
//                            + " or lower(e.email) like :keyword"
//                            + " or lower(e.code) like :keyword"
//                            + " or lower(e.fullName) like :keyword"
//                            + " or lower(e.phoneNumber) like :keyword"
//                            + " or lower(r.name) like :keyword ) ");
//            values.put("keyword", "%" + SqlUtils.encodeKeyword(request.getKeyword().toLowerCase()) + "%");
//        }
//
//        // Filter by role
//        if (StringUtils.isNotBlank(request.getType())) {
//            sql.append(" and r.code = :roleCode ");
//            values.put("roleCode", request.getType());
//        }
//
//        // Filter by statuses
//        if (!CollectionUtils.isEmpty(request.getStatuses())) {
//            sql.append(" and e.status in :statuses ");
//            values.put("statuses", request.getStatuses());
//        }
//
//        return sql.toString();
//    }
//
//    // Create order by clause
//    private StringBuilder createOrderQuery(String sortBy) {
//        StringBuilder hql = new StringBuilder(" ");
//        if (StringUtils.hasLength(sortBy)) {
//            hql.append(" order by e.").append(sortBy.replace(".", " "));
//        } else {
//            hql.append(" order by e.lastModifiedAt desc ");
//        }
//        return hql;
//    }
}
