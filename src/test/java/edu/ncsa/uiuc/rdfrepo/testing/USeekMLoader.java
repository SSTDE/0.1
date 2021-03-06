package edu.ncsa.uiuc.rdfrepo.testing;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailException;

import edu.ncsa.sstde.indexing.IndexingSail;

public class USeekMLoader {
	private Repository repository = null;
	private RepositoryConnection connection = null;
	
    public static void main(String[] args) throws RepositoryException, RDFParseException, IOException, SailException {
        String dir = "repo2";
        String dburl = "jdbc:postgresql://localhost:5432/useekm";
        String dbuser = "postgres";
        String password = "zsmpM942";
        String capturepredicate = "http://www.opengis.net/rdf#hasWKT";
        IndexingSail sail = USeekMSailFac.getNativeIndexingSail(dir, dburl, dbuser, password, capturepredicate);
        //        SailRepository repository = new SailRepository(sail);
        //        repository.getConnection().clear();

        //        sail.initialize();
        USeekMLoader loader = new USeekMLoader(sail);

        Date date = new Date();

        File file = new File("C:\\work\\programming\\OData\\LinkedData\\xml\\rdf_wkt");
        loader.loadDirectory(file, RDFFormat.RDFXML);

        Date date2 = new Date();

        long time = date2.getTime() - date.getTime();
        System.out.println(time);
        System.out.println(time / 1000);

	}
	
    public USeekMLoader(Sail sail) {
        this.repository = new SailRepository(sail);
		
	}
	
	public void loadDirectory(String dir, RDFFormat format) throws RepositoryException, RDFParseException, IOException {
		this.connection = this.repository.getConnection();
		File file = new File(dir);
		loadDirectory(file, format);
		this.connection.close();
	}

	private RepositoryConnection getConnection() throws RepositoryException{
		if (this.connection == null) {
			this.connection = this.repository.getConnection();
		}
		return this.connection;
	}
	
	public void loadDirectory(File file, RDFFormat format) throws RDFParseException, RepositoryException, IOException {
		if (file.isDirectory()) {
			for (File file2 : file.listFiles()) {
				loadDirectory(file2, format);
			}
		}else{
			loadRDF(file, format);
		}
	}

	private void loadRDF(File file, RDFFormat format) throws RDFParseException, RepositoryException, IOException {
        //        this.repository.initialize();
        try {
            System.out.println("start loading " + file.getName());
            getConnection().setAutoCommit(true);
            //            SailRepository repository = new SailRepository(sail);
            //            SailRepositoryConnection connection = repository.getConnection();
            //            connection.add(file, "", RDFFormat.RDFXML);
            //            SailConnection connection2;
            //            
            getConnection().add(file, "", RDFFormat.RDFXML);
            //            RepositoryConnection connection;
            //            connection.ad
            //            getConnection().commit();
            System.out.println(file.getName() + " has been loaded");
            //            getConnection().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

	}

    //	public void initStore() throws RepositoryException{
    //		File dataDir = new File("repo");
    //		Repository myRepository = new SailRepository( new NativeStore(dataDir) );
    //		myRepository.initialize();
    //	}
}
