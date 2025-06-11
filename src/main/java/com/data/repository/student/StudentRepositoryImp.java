package com.data.repository.student;

import com.data.model.Student;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StudentRepositoryImp implements StudentRepository {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public boolean save(Student student) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            session.save(student);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean checkExistUserName(String userName) {
        Session session = sessionFactory.openSession();

        Query<Long> query = session.createQuery("select count(*) from Student where userName = :userName", Long.class);
        query.setParameter("userName", userName);

        Long count = query.getSingleResult();
        return count > 0;
    }

    @Override
    public boolean checkExistEmail(String email) {
        Session session = sessionFactory.openSession();

        Query<Long> query = session.createQuery("select count(*) from Student where email = :email", Long.class);

        query.setParameter("email", email);

        Long count = query.getSingleResult();
        return count > 0;
    }

    @Override
    public boolean checkExistPhone(String phone) {
        Session session = sessionFactory.openSession();

        Query<Long> query = session.createQuery("select count(*) from Student where phone = :phone", Long.class);

        query.setParameter("phone", phone);

        Long count = query.getSingleResult();
        return count > 0;
    }

    @Override
    public Student checkLogin(String email, String password) {
        Session session = sessionFactory.openSession();

        Query<Student> query = session.createQuery("from Student where email = :email and password = :password", Student.class);
        query.setParameter("email", email);
        query.setParameter("password", password);
        List<Student> resultList = query.getResultList();
        return resultList.isEmpty() ? null : resultList.get(0);
    }
}
