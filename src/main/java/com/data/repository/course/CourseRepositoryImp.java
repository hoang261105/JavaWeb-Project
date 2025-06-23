package com.data.repository.course;

import com.data.model.Course;
import com.data.model.Status;
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

        try {
            Query<Course> query = session.createQuery("from Course", Course.class);

            if (page < 1) page = 1;
            query.setFirstResult((page - 1) * size);
            query.setMaxResults(size);

            return query.getResultList();
        }finally {
            session.close();
        }
    }

    @Override
    public long count() {
        Session session = sessionFactory.openSession();

        try {
            Query<Long> query = session.createQuery("select count(*) from Course", Long.class);

            return query.getSingleResult();
        }finally {
            session.close();
        }
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
        } finally {
            session.close();
        }
        return false;
    }

    @Override
    public boolean checkExistCourseId(String courseId) {
        Session session = sessionFactory.openSession();

        try {
            Query<Long> query = session.createQuery("select count(*) from Course where courseId = :courseId", Long.class);
            query.setParameter("courseId", courseId);
            Long count = query.getSingleResult();

            return count > 0;
        }finally {
            session.close();
        }
    }

    @Override
    public boolean checkExistCourseName(String courseName, String courseId) {
        Session session = sessionFactory.openSession();
        
        try {
            String hql = "select count(c) from Course c where c.courseName = :courseName";

            if (courseId != null && !courseId.isEmpty()) {
                hql += " and c.courseId != :courseId";
            }
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("courseName", courseName);

            if (courseId != null && !courseId.isEmpty()) {
                query.setParameter("courseId", courseId);
            }

            Long count = query.getSingleResult();
            return count > 0;
        }finally {
            session.close();
        }
    }

    @Override
    public Course findById(String courseId) {
        Session session = sessionFactory.openSession();

        try {
            Query<Course> query = session.createQuery("from Course where courseId = :courseId", Course.class);
            query.setParameter("courseId", courseId);

            return query.getSingleResult();
        }finally {
            session.close();
        }
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
        } finally {
            session.close();
        }
    }

    @Override
    public List<Course> findCourseByName(String courseName, int page, int size) {
        Session session = sessionFactory.openSession();

        try {
            Query<Course> query = session.createQuery("from Course where courseName like concat('%', :courseName, '%') ", Course.class);
            query.setParameter("courseName", courseName);

            if (page < 1) page = 1;
            query.setFirstResult((page - 1) * size);
            query.setMaxResults(size);
            return query.getResultList();
        }finally {
            session.close();
        }
    }

    @Override
    public long countCourseByName(String courseName) {
        Session session = sessionFactory.openSession();

        try {
            Query<Long> query = session.createQuery("select count(*) from Course where courseName like concat('%', :courseName, '%')", Long.class);
            query.setParameter("courseName", courseName);
            return query.getSingleResult();
        }finally {
            session.close();
        }
    }

    @Override
    public List<Course> sortCourseById(String sortType, int page, int size) {
        Session session = sessionFactory.openSession();

        try {
            String hql = "from Course";

            if (sortType.equals("asc")){
                hql += " order by courseId";
            } else if (sortType.equals("desc")){
                hql += " order by courseId desc";
            }

            Query<Course> query = session.createQuery(hql, Course.class);

            if (page < 1) page = 1;
            query.setFirstResult((page - 1) * size);
            query.setMaxResults(size);
            List<Course> courses = query.getResultList();
            return courses;
        }finally {
            session.close();
        }
    }

    @Override
    public List<Course> sortCourseByName(String sortType, int page, int size) {
        Session session = sessionFactory.openSession();
        try {
            String hql = "from Course";

            if (sortType.equals("asc")){
                hql += " order by courseName";
            } else if (sortType.equals("desc")){
                hql += " order by courseName desc";
            }

            Query<Course> query = session.createQuery(hql, Course.class);

            if (page < 1) page = 1;
            query.setFirstResult((page - 1) * size);
            query.setMaxResults(size);
            List<Course> courses = query.getResultList();
            return courses;
        }finally {
            session.close();
        }
    }

    @Override
    public void updateStatus(String courseId) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Course course = session.get(Course.class, courseId);

            if (course != null){
                course.setStatus(!course.isStatus());
                session.update(course);
                transaction.commit();
            }
        }finally {
            session.close();
        }
    }

    @Override
    public List<Course> getEnrolledCoursesByStudent(int studentId, int page, int size) {
        Session session = sessionFactory.openSession();
        try {
            Query<Course> query = session.createQuery(
                    "SELECT DISTINCT c FROM Course c " +
                            "LEFT JOIN Enrollment e ON c.courseId = e.course.courseId AND e.student.studentId = :studentId AND e.status != :cancelled " +
                            "WHERE c.status = true OR e.id IS NOT NULL",
                    Course.class
            );
            query.setParameter("studentId", studentId);
            query.setParameter("cancelled", Status.CANCELLED);

            if (page < 1) page = 1;
            query.setFirstResult((page - 1) * size);
            query.setMaxResults(size);

            return query.getResultList();
        } finally {
            session.close();
        }
    }

    @Override
    public long countEnrolledCoursesByStudent(int studentId) {
        Session session = sessionFactory.openSession();
        try {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(DISTINCT c.courseId) FROM Course c " +
                            "LEFT JOIN Enrollment e ON c.courseId = e.course.courseId AND e.student.studentId = :studentId AND e.status != :cancelled " +
                            "WHERE c.status = true OR e.id IS NOT NULL",
                    Long.class
            );
            query.setParameter("studentId", studentId);
            query.setParameter("cancelled", Status.CANCELLED);
            return query.getSingleResult();
        } finally {
            session.close();
        }
    }
}
