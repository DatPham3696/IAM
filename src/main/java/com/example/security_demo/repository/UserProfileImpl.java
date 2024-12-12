package com.example.security_demo.repository;

import com.example.security_demo.dto.request.user.UserSearchRequest;
import com.example.security_demo.dto.request.userProfile.UserProfileSearchRequest;
import com.example.security_demo.entity.User;
import com.example.security_demo.entity.UserProfile;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserProfileImpl implements IUserProfileRepositoryCustom{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<UserProfile> searchUser(UserProfileSearchRequest request) {
        Map<String, Object> values = new HashMap<>();
        String sql = "select e from UserProfile e " + createWhereQuery(request, values) + createOrderQuery(request.getSort());
        Query query = entityManager.createQuery(sql, UserProfile.class);
        values.forEach(query::setParameter);
        query.setFirstResult((request.getPage() - 1) * request.getSize());
        query.setMaxResults(request.getSize());
        return query.getResultList();
    }


    private String createWhereQuery(UserProfileSearchRequest request, Map<String, Object> values) {
        StringBuilder sql = new StringBuilder();
        boolean hasWhereClause = false;

        if (StringUtils.hasLength(request.getKeyword())) {
            sql.append(" where lower(e.username) like :keyword");
            values.put("keyword", "%" + request.getKeyword().toLowerCase() + "%");
            hasWhereClause = true;
        }

        if (StringUtils.hasLength(request.getUserName())) {
            if (hasWhereClause) {
                sql.append(" and e.username = :username");
            } else {
                sql.append(" where e.username = :username");
                hasWhereClause = true;
            }
            values.put("username", request.getUserName());
        }
        return sql.toString();
    }

    private StringBuilder createOrderQuery(String sortBy) {
        StringBuilder hql = new StringBuilder(" ");
        if (StringUtils.hasLength(sortBy)) {
            hql.append(" order by e.").append(sortBy.replace(".", " "));
        } else {
            hql.append(" order by e.username desc ");
        }
        return hql;
    }
}
