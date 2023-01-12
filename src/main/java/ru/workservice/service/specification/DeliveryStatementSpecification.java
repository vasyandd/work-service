package ru.workservice.service.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.workservice.model.entity.Contract;
import ru.workservice.model.entity.DeliveryStatement;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class DeliveryStatementSpecification {
    public static Specification<DeliveryStatement> eqByContract(Contract contract) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("contract").get("contractNumber"), contract.getContractNumber()));
            predicates.add(cb.equal(root.get("contract").get("contractDate"), contract.getContractDate()));
            predicates.add(cb.equal(root.get("contract").get("additionalAgreement"), contract.getAdditionalAgreement()));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
