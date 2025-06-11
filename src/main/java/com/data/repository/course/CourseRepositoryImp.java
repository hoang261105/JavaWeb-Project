package com.data.repository.course;

import com.data.model.Course;
import com.data.model.Student;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CourseRepositoryImp implements CourseRepository {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Course> findAll(int page, int size) {
        Session session = sessionFactory.openSession();

        Query<Course> query = session.createQuery("from Course", Course.class);

        if (page < 1) page = 1;
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);

        return query.getResultList();
    }

    @Override
    public long count() {
        Session session = sessionFactory.openSession();

        Query<Long> query = session.createQuery("select count(*) from Course", Long.class);

        return query.getSingleResult();
    }

    @Override
    public boolean save(Course course) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            session.saveOrUpdate(course);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean checkExistCourseId(String courseId) {
        Session session = sessionFactory.openSession();

        Query<Long> query = session.createQuery("select count(*) from Course where courseId = :courseId", Long.class);
        query.setParameter("courseId", courseId);
        Long count = query.getSingleResult();

        return count > 0;
    }

    @Override
    public boolean checkExistCourseName(String courseName, String courseId) {
        Session session = sessionFactory.openSession();
        
        String hql = "select count(c) from Course c where c.courseName = :courseName";

        if (courseId != null && !courseId.isEmpty()) {
            hql += " and c.courseId = :courseId";
        }
        Query<Long> query = session.createQuery(hql, Long.class);
        query.setParameter("courseName", courseName);

        if (courseId != null && !courseId.isEmpty()) {
            query.setParameter("courseId", courseId);
        }

        Long count = query.getSingleResult();
        return count > 0;
    }

    @Override
    public Course findById(String courseId) {
        Session session = sessionFactory.openSession();

        Query<Course> query = session.createQuery("from Course where courseId = :courseId", Course.class);
        query.setParameter("courseId", courseId);

        return query.getSingleResult();
    }

    @Override
    public void delete(String courseId) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Course course = findById(courseId);
            session.delete(course);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
