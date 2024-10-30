package repository;


import domain.Deposit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepositRepository extends CrudRepository<Deposit, Long> {


    @Query(value="SELECT * FROM DEPOSIT WHERE ACCOUNT_ID = ?1", nativeQuery = true)
    public Iterable<Deposit> findAllByAccountId(Long accountId);
}
