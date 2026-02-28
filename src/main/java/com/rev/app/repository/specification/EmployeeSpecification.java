package com.rev.app.repository.specification;

import com.rev.app.entity.Employee;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class EmployeeSpecification {

    public static Specification<Employee> filterEmployees(String departmentName, String designationName, String search) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (departmentName != null && !departmentName.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("department").get("departmentName"), departmentName));
            }

            if (designationName != null && !designationName.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("designation").get("designationName"), designationName));
            }

            if (search != null && !search.isEmpty()) {
                String likeSearch = "%" + search.toLowerCase() + "%";
                Predicate nameSearch = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), likeSearch),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), likeSearch),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("empId")), likeSearch)
                );
                predicates.add(nameSearch);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
