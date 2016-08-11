package com.cooksys.serialization.assignment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.cooksys.serialization.assignment.model.Contact;
import com.cooksys.serialization.assignment.model.Instructor;
import com.cooksys.serialization.assignment.model.Session;
import com.cooksys.serialization.assignment.model.Student;

public class Main {

	/**
	 * Creates a {@link Student} object using the given studentContactFile. The studentContactFile should be an XML file containing the marshaled form of a {@link Contact} object.
	 *
	 * @param studentContactFile
	 *            the XML file to use
	 * @param jaxb
	 *            the JAXB context to use
	 * @return a {@link Student} object built using the {@link Contact} data in the given file
	 * @throws JAXBException
	 */
	public static Student readStudent(File studentContactFile, JAXBContext jaxb) {
		Contact stCon = null;

		try {
			Unmarshaller unmarshal = jaxb.createUnmarshaller();
			stCon = (Contact) unmarshal.unmarshal(studentContactFile);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		Student st = new Student();
		st.setContact(stCon);

		return st;
	}

	/**
	 * Creates a list of {@link Student} objects using the given directory of student contact files.
	 *
	 * @param studentDirectory
	 *            the directory of student contact files to use
	 * @param jaxb
	 *            the JAXB context to use
	 * @return a list of {@link Student} objects built using the contact files in the given directory
	 * @throws JAXBException
	 */
	public static List<Student> readStudents(File studentDirectory, JAXBContext jaxb) {
		List<Student> sts = new ArrayList<>();

		File[] files = studentDirectory.listFiles();

		for (File file : files) {
			sts.add(readStudent(file, jaxb));
		}
		return sts;
	}

	/**
	 * Creates an {@link Instructor} object using the given instructorContactFile. The instructorContactFile should be an XML file containing the marshaled form of a {@link Contact} object.
	 *
	 * @param instructorContactFile
	 *            the XML file to use
	 * @param jaxb
	 *            the JAXB context to use
	 * @return an {@link Instructor} object built using the {@link Contact} data in the given file
	 * @throws JAXBException
	 */
	public static Instructor readInstructor(File instructorContactFile, JAXBContext jaxb) {
		Contact instCon = null;

		try {
			Unmarshaller unmarshal = jaxb.createUnmarshaller();
			instCon = (Contact) unmarshal.unmarshal(instructorContactFile);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		Instructor inst = new Instructor();
		inst.setContact(instCon);

		return inst;
	}

	/**
	 * Creates a {@link Session} object using the given rootDirectory. A {@link Session} root directory is named after the location of the {@link Session}, and contains a directory named after the start date of the {@link Session}. The start date directory in turn contains a directory named `students`, which contains contact files for the students in the session. The start date directory also contains an instructor contact file named `instructor.xml`.
	 *
	 * @param rootDirectory
	 *            the root directory of the session data, named after the session location
	 * @param jaxb
	 *            the JAXB context to use
	 * @return a {@link Session} object built from the data in the given directory
	 * @throws JAXBException
	 */
	public static Session readSession(File rootDirectory, JAXBContext jaxb) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(Contact.class);
		Session se = new Session();
		File[] fs = rootDirectory.listFiles();

		for (File f : fs) {
			if (f.isDirectory()) {
				se.setLocation(f.getName());
				File[] fs1 = f.listFiles();
				for (File f1 : fs1) {
					if (f1.isDirectory()) {
						se.setStartDate(f1.getName());
						File[] fs2 = f1.listFiles();
						for (File f2 : fs2) {
							if (f2.isFile()) {
								se.setInstructor(readInstructor((f2), context));
							}
							if (f2.isDirectory()) {
								se.setStudents(readStudents((f2), context));
							}
						}
					}
				}
			}
		}

		return se;
	}

	/**
	 * Writes a given session to a given XML file
	 *
	 * @param session
	 *            the session to write to the given file
	 * @param sessionFile
	 *            the file to which the session is to be written
	 * @param jaxb
	 *            the JAXB context to use
	 * @throws JAXBException
	 */
	public static void writeSession(Session session, File sessionFile, JAXBContext jaxb) {
		try {
			Marshaller marshal = jaxb.createMarshaller();
			marshal.marshal(session, sessionFile);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Main Method Execution Steps: 1. Configure JAXB for the classes in the com.cooksys.serialization.assignment.model package 2. Read a session object from the <project-root>/input/memphis/ directory using the methods defined above 3. Write the session object to the <project-root>/output/session.xml file.
	 *
	 * JAXB Annotations and Configuration: You will have to add JAXB annotations to the classes in the com.cooksys.serialization.assignment.model package
	 *
	 * Check the XML files in the <project-root>/input/ directory to determine how to configure the {@link Contact} JAXB annotations
	 *
	 * The {@link Session} object should marshal to look like the following: <session location="..." start-date="..."> <instructor> <contact>... </contact> </instructor> <students> ... <student> <contact>...</contact> </student> ... </students> </session>
	 * 
	 * @throws JAXBException
	 */
	public static void main(String[] args) throws JAXBException {
		try {
			JAXBContext context = JAXBContext.newInstance(Session.class);
			writeSession(readSession(new File("input"), context), new File("output/session.xml"), context);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
}
