package com.example.security_demo.repository;

import com.example.security_demo.dto.request.Page.SearchRequest;
import com.example.security_demo.dto.request.user.UserSearchRequest;
import com.example.security_demo.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserRepositoryImpl implements IUserRepositoryCustom{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<User> searchUser(UserSearchRequest request) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select e from User e " + createWhereQuery(request, values) + createOrderQuery(request.getSort());
        Query query = entityManager.createQuery(sql, User.class);
        values.forEach(query::setParameter);
        query.setFirstResult((request.getPage() - 1) * request.getSize());
        query.setMaxResults(request.getSize());
        return query.getResultList();
    }

    @Override
    public Long countUser(UserSearchRequest request) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select count(e) from User e " + createWhereQuery(request, values);
        Query query = entityManager.createQuery(sql, Long.class);
        values.forEach(query::setParameter);
        return (Long) query.getSingleResult();
    }

    private String createWhereQuery(UserSearchRequest request, Map<String, Object> values) {
        StringBuilder sql = new StringBuilder();
        sql.append(" where e.deleted = false");

        if (StringUtils.hasLength(request.getKeyword())) {
            sql.append(" and ( lower(e.userName) like :keyword or lower(e.email) like :keyword )");
            values.put("keyword", "%" + request.getKeyword().toLowerCase() + "%");
        }

        if (StringUtils.hasLength(request.getUserName())) {
            sql.append(" and e.userName = :userName ");
            values.put("userName", request.getUserName());
        }
        if (StringUtils.hasLength(request.getEmail())) {
            sql.append(" and e.email = :email ");
            values.put("email", request.getEmail());
        }

//        if (!CollectionUtils.isEmpty(request.getStatuses())) {
//            sql.append(" and e.status in :statuses ");
//            values.put("statuses", request.getStatuses());
//        }
        return sql.toString();
    }

    private StringBuilder createOrderQuery(String sortBy) {
        StringBuilder hql = new StringBuilder(" ");
        if (StringUtils.hasLength(sortBy)) {
            hql.append(" order by e.").append(sortBy.replace(".", " "));
        } else {
            hql.append(" order by e.lastModifiedDate desc ");
        }
        return hql;
    }
}
