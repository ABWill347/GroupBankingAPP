package repository;


import domain.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    @Query(value="SELECT * FROM BILL WHERE ACCOUNT_ID = ?1", nativeQuery = true)
    public List<Bill> findByAccountId(Long accountId);
}
