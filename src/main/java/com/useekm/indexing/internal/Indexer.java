/*
 * Copyright 2010 by TalkingTrends (Amsterdam, The Netherlands)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://opensahara.com/licenses/apache-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.useekm.indexing.internal;

import java.util.Collection;

import info.aduna.iteration.CloseableIteration;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.Dataset;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.algebra.TupleExpr;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;
import com.useekm.indexing.exception.IndexException;

import edu.ncsa.sstde.indexing.IndexerSettings;
import edu.ncsa.sstde.indexing.IndexingSail;
import edu.ncsa.sstde.indexing.IndexingSailConnection;
import edu.ncsa.sstde.indexing.algebra.IndexerExpr;

/**
 * An Indexer is used by an {@link IndexingSailConnection} to provide external
 * indexes for some types of statements, and to provide a query interface to use
 * those indexes from SPARQL and SeRQL queries.
 * <p>
 * Access to the Indexer is not thread-safe. Concurrent access to an index
 * should be handled with multiple instantiations of an {@link Indexer}. In
 * practice this means that every {@link RepositoryConnection} should have its
 * own {@link Indexer} instance.
 * 
 * @see IndexingSailConnection
 * @see IndexingSail
 */
public interface Indexer {
	/**
	 * Checks whether the added statement needs an index, and if so, adds the
	 * index. Called by {@link IndexingSailConnection} when a statement is added
	 * to the store.
	 * 
	 * @param subj
	 *            Statement subject.
	 * @param pred
	 *            Statement predicate.
	 * @param obj
	 *            Statement object.
	 * @param ctx
	 *            Optional statement contexts.
	 * 
	 * @return true If the statement is available in the index after the
	 *         operation.
	 * 
	 * @throws IndexException
	 *             When the addIndex fails.
	 */
	// boolean addIndex(SailConnection connection, Resource subj, URI pred,
	// Value obj, Resource... ctx);

	/**
	 * @param connection
	 *            the sail connection which is contains the raw data, it is
	 *            needed for querying the patterns to be added by the indexers.
	 * @param statements
	 *            the statements to add
	 * @return if all the statements are added successfully
	 */
	boolean executeBatchAdd(SailConnection connection,
			Collection<Statement> statements);

	/**
	 * @param connection
	 *            the sail connection which is contains the raw data, it is
	 *            needed for querying the patterns to be removed by the
	 *            indexers.
	 * @param statements
	 *            the statements to remove
	 * @return if all the statements are removed successfully
	 */
	boolean executeBatchRemove(SailConnection connection,
			Collection<Statement> statements);

	/**
	 * @return the name of the indexer
	 */
	String getName();

	/**
	 * @param name
	 *            : the name of the indexer
	 */
	void setName(String name);

	/**
	 * Flushes recently changed statement additions or removals to the index.
	 * Called by {@link IndexingSailConnection} when a flush is done.
	 * 
	 * @throws IndexException
	 *             When the flush fails.
	 */
	void flush();

	/**
	 * Commits actions executed by
	 * {@link #addIndex(Resource, URI, Value, Resource...)} and
	 * {@link #removeIndex(Resource, URI, Value, Resource...)}. Called by
	 * {@link IndexingSail} when a transaction on the
	 * {@link IndexingSailConnection} is committed.
	 * 
	 * @throws IndexException
	 *             When the commit fails.
	 */
	void commit();

	/**
	 * Commits actions executed by
	 * {@link #addIndex(Resource, URI, Value, Resource...)} and
	 * {@link #removeIndex(Resource, URI, Value, Resource...)}. Called by
	 * {@link IndexingSail} when a transaction on the
	 * {@link IndexingSailConnection} is committed.
	 * 
	 * @throws IndexException
	 *             When the rollback fails.
	 */
	void rollback();

	/**
	 * Closes this indexer. Uncommitted operations will be rolled back.
	 * 
	 * @throws IndexException
	 *             When the close fails.
	 */
	void close();

	/**
	 * @return The settings used to create this indexer.
	 */
	IndexerSettings getSettings();

	/**
	 * Removes all statements from the index that are either not available in
	 * the triple store, or should not be indexed according to the current
	 * settings. Adds all statements to the index that should be indexed
	 * according to the current settings. Thus, this is a complete rebuild of
	 * the index, and possibly a very expensive operation.
	 * 
	 * A reindex might not be safe to do when other
	 * {@link IndexingSailConnection} are open. Please check the documentation
	 * of the implementing class for the safety of this operation with multiple
	 * active connections.
	 * 
	 * @param connection
	 *            The connection to use for the operation.
	 */
	void reindex(SailConnection connection) throws SailException,
			IndexException;

	/**
	 * Creates an iterator that iterates over all permutations of all results of
	 * the queries listed in the argument.
	 * 
	 * @param valueFactory
	 *            {@link ValueFactory} to use for creating result resources.
	 * @param queries
	 *            The queries to perform on the index
	 * @param bindings
	 *            The variable bindings to use.
	 */
	CloseableIteration<BindingSet, QueryEvaluationException> iterator(
			ValueFactory valueFactory, IndexerExpr queries, BindingSet bindings)
			throws QueryEvaluationException;

	/**
	 * provides a hook to further optimize or alter the given {@link TupleExpr},
	 * before evaluation by the unerlying {@link Sail} and {@link Indexer}.
	 */
	TupleExpr optimize(TupleExpr tupleExpr, Dataset dataset, BindingSet bindings);

	void initialize();

	/**
	 * @param sailConnection
	 *            the connection to the sail repository which host the real data
	 * @param toAdd
	 *            the new triples added to the sail repository
	 */
	void addBatch(SailConnection sailConnection, Collection<Statement> toAdd);

	/**
	 * @param sailConnection
	 *            the connection to the sail repository which host the real data
	 * @param toRemove
	 *            the triples to be removed from the sail repository
	 */
	void removeBatch(SailConnection sailConnection,
			Collection<Statement> toRemove);

	/**
	 * clear the index
	 */
	void clear();
}
