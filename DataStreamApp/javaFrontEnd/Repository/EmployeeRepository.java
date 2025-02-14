package com.project.homework2.Repository;

import com.project.homework2.Model.Emp;
import com.project.homework2.Model.EmpDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Emp, Long> {
    @Query("SELECT new com.project.homework2.Model.EmpDTO(e.id, e.empno, e.ename, m.ename, e.sal, e.comm, d.dname, e.img, e.job, e.mgr, e.hiredate, e.deptno) " +
            "FROM Emp e " +
            "LEFT JOIN Emp m ON e.mgr = m.empno " +
            "LEFT JOIN Dept d ON e.deptno = d.deptno")
    List<EmpDTO> findAllEmployeesWithDeptAndMgr();
}