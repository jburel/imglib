package net.imglib2.ops.knip.util;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RulebasedLabelFilter< L extends Comparable< L >> implements LabelFilter< L >, Externalizable
{

	public enum Operator
	{
		OR, AND, XOR
	}

	private List< String > m_rules;

	private Operator m_op = Operator.OR;

	private List< L > m_tmpLabeling;

	private BitSet m_ruleValidation;

	private Map< L, Set< Integer >> m_validLabels;

	private Set< L > m_invalidLabels;

	public RulebasedLabelFilter( String[] rules, Operator op )
	{

		this();

		if ( rules != null )
			addRules( rules );

		if ( op != null )
			m_op = op;
	}

	public RulebasedLabelFilter()
	{
		m_tmpLabeling = new ArrayList< L >();
		m_rules = new ArrayList< String >();
		m_ruleValidation = new BitSet();
		m_validLabels = new HashMap< L, Set< Integer >>();
		m_invalidLabels = new HashSet< L >();
	}

	public final boolean addRules( String... rules )
	{
		boolean added = false;
		for ( String r : rules )
		{
			added = m_rules.add( r ) || added;
		}

		m_invalidLabels.clear();
		m_validLabels.clear();
		return added;
	}

	@Override
	public int hashCode()
	{

		int hashCode = 1;

		for ( String rule : m_rules )
		{
			hashCode *= 31;
			hashCode += rule.hashCode();
		}

		hashCode = hashCode * 31 + m_op.hashCode();

		return hashCode;
	}

	@Override
	public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException
	{
		int num = in.readInt();
		for ( int i = 0; i < num; i++ )
		{
			m_rules.add( in.readUTF() );
		}

		m_op = Operator.values()[ in.readInt() ];
	}

	@Override
	public void writeExternal( ObjectOutput out ) throws IOException
	{
		out.writeInt( m_rules.size() );
		for ( int i = 0; i < m_rules.size(); i++ )
		{
			out.writeUTF( m_rules.get( i ) );
		}

		out.writeInt( m_op.ordinal() );
	}

	public void setOp( Operator op )
	{
		m_validLabels.clear();
		m_invalidLabels.clear();
		m_ruleValidation.clear();
		m_op = op;
	}

	public List< String > getRules()
	{
		return m_rules;
	}

	public Operator getOp()
	{
		return m_op;
	}

	public List< L > filterLabeling( List< L > labels, Operator op, List< String > rules )
	{

		if ( rules.size() == 0 ) { return labels; }

		m_ruleValidation.clear();
		m_tmpLabeling.clear();

		for ( L label : labels )
		{

			if ( m_invalidLabels.contains( label ) )
			{
				continue;
			}

			if ( m_validLabels.containsKey( label ) )
			{
				m_tmpLabeling.add( label );

				if ( op == Operator.OR )
				{
					continue;
				}
				else if ( op == Operator.AND )
				{
					for ( int i : m_validLabels.get( label ) )
					{
						m_ruleValidation.set( i );
					}
				}
				else if ( op == Operator.XOR )
				{
					if ( m_tmpLabeling.size() > 1 )
					{
						m_tmpLabeling.clear();
						return m_tmpLabeling;
					}
				}

			}
			else
			{

				int r = 0;
				String labelString = label.toString();

				for ( String rule : rules )
				{

					if ( labelString.matches( rule ) )
					{
						m_tmpLabeling.add( label );
						m_validLabels.put( label, new HashSet< Integer >() );
						m_invalidLabels.remove( label );

						if ( op == Operator.OR )
							break;

						if ( op == Operator.XOR )
						{
							if ( m_tmpLabeling.size() > 1 )
							{
								m_tmpLabeling.clear();
								return m_tmpLabeling;
							}
						}
						else if ( op == Operator.AND )
						{
							m_ruleValidation.set( r );
							m_validLabels.get( label ).add( r );

						}
					}
					r++;
				}

				if ( !m_validLabels.containsKey( label ) )
				{
					m_invalidLabels.add( label );
				}
			}
		}

		switch ( op )
		{
		case AND:
			if ( m_ruleValidation.cardinality() != m_rules.size() )
			{
				m_tmpLabeling.clear();
			}

		}

		return m_tmpLabeling;
	}

	public static < L extends Comparable< L >> boolean isValid( L label, String rule )
	{
		return label.toString().matches( rule );
	}

	public boolean isValid( L label )
	{
		for ( String rule : m_rules )
		{
			if ( label.toString().matches( rule ) )
				return true;
		}
		return m_rules.size() == 0;
	}

	@Override
	public List< L > filterLabeling( List< L > labels )
	{
		return filterLabeling( labels, m_op, m_rules );
	}

	public static String formatRegExp( String rule )
	{

		rule = rule.trim();
		rule = rule.replaceAll( "\\.", "\\\\." );
		rule = rule.replaceAll( "[^a-zA-Z0-9*|&_?()\t\r\n:\\.\\ ]", "" );
		rule = rule.replaceAll( "\\*", ".*" );
		rule = rule.replaceAll( "\\?", "." );
		rule = rule.replaceAll( "\\(", "\\\\\\(" );
		rule = rule.replaceAll( "\\)", "\\\\\\)" );

		String regExp = "(";
		regExp += rule;
		regExp += ")";

		return regExp;
	}

	@Override
	public void clear()
	{
		m_rules.clear();
	}

	public RulebasedLabelFilter< L > copy()
	{
		return new RulebasedLabelFilter< L >( m_rules.toArray( new String[ m_rules.size() ] ), m_op );
	}

}
