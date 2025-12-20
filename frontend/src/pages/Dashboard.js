import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { dashboardAPI } from '../services/api';
import './Dashboard.css';

const Dashboard = () => {
    const [dashboard, setDashboard] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [selectedAccountId, setSelectedAccountId] = useState(null);
    const [currentPage, setCurrentPage] = useState(0);
    const [transactions, setTransactions] = useState(null);

    const navigate = useNavigate();

    useEffect(() => {
        loadDashboard();
    }, [selectedAccountId]);

    const loadDashboard = async () => {
        try {
            setLoading(true);
            const response = await dashboardAPI.get(selectedAccountId);
            setDashboard(response.data.data);

            if (response.data.data.account) {
                loadTransactions(response.data.data.account.id, 0);
            }
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    const loadTransactions = async (accountId, page) => {
        try {
            const response = await dashboardAPI.getTransactions(accountId, page, 10);
            setTransactions(response.data.data);
            setCurrentPage(page);
        } catch (err) {
            console.error('Error loading transactions:', err);
        }
    };

    const handleAccountChange = (e) => {
        setSelectedAccountId(e.target.value ? parseInt(e.target.value) : null);
        setCurrentPage(0);
    };

    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('fr-MA', {
            style: 'currency',
            currency: 'MAD',
        }).format(amount);
    };

    const formatDate = (dateString) => {
        return new Date(dateString).toLocaleDateString('fr-FR', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
        });
    };

    if (loading) {
        return (
            <div className="dashboard-loading">
                <div className="loader"></div>
                <p>Chargement de votre tableau de bord...</p>
            </div>
        );
    }

    if (error) {
        return (
            <div className="dashboard-error">
                <p className="alert alert-error">{error}</p>
            </div>
        );
    }

    if (!dashboard?.allAccounts?.length) {
        return (
            <div className="dashboard-empty">
                <h2>Aucun compte trouvé</h2>
                <p>Vous n'avez pas encore de compte bancaire associé.</p>
            </div>
        );
    }

    return (
        <div className="dashboard animate-fadeIn">
            <div className="dashboard-header">
                <div>
                    <h1 className="dashboard-title">Tableau de Bord</h1>
                    <p className="dashboard-subtitle">Bienvenue, voici l'état de vos comptes</p>
                </div>
                <button
                    className="btn btn-primary"
                    onClick={() => navigate('/new-transfer')}
                >
                    + Nouveau Virement
                </button>
            </div>

            {/* Account Selector */}
            {dashboard.allAccounts.length > 1 && (
                <div className="account-selector">
                    <label className="form-label">Sélectionner un compte</label>
                    <select
                        className="form-select"
                        value={selectedAccountId || dashboard.account?.id || ''}
                        onChange={handleAccountChange}
                    >
                        {dashboard.allAccounts.map(account => (
                            <option key={account.id} value={account.id}>
                                {account.rib} - {formatCurrency(account.balance)}
                            </option>
                        ))}
                    </select>
                </div>
            )}

            {/* Stats Cards */}
            <div className="stats-grid">
                <div className="stat-card stat-balance">
                    <div className="stat-icon">💰</div>
                    <div className="stat-info">
                        <span className="stat-label">Solde du compte</span>
                        <span className="stat-value">{formatCurrency(dashboard.account?.balance || 0)}</span>
                    </div>
                </div>

                <div className="stat-card stat-total">
                    <div className="stat-icon">📊</div>
                    <div className="stat-info">
                        <span className="stat-label">Solde total</span>
                        <span className="stat-value">{formatCurrency(dashboard.totalBalance || 0)}</span>
                    </div>
                </div>

                <div className="stat-card stat-rib">
                    <div className="stat-icon">🏦</div>
                    <div className="stat-info">
                        <span className="stat-label">RIB</span>
                        <span className="stat-value stat-rib-value">{dashboard.account?.rib || 'N/A'}</span>
                    </div>
                </div>

                <div className="stat-card stat-status">
                    <div className="stat-icon">✅</div>
                    <div className="stat-info">
                        <span className="stat-label">Statut</span>
                        <span className={`badge badge-${dashboard.account?.status?.toLowerCase()}`}>
                            {dashboard.account?.status === 'OPEN' ? 'Ouvert' : dashboard.account?.status}
                        </span>
                    </div>
                </div>
            </div>

            {/* Transactions Table */}
            <div className="transactions-section card">
                <div className="card-header">
                    <h2 className="card-title">Dernières Opérations</h2>
                </div>

                {dashboard.recentTransactions?.length > 0 ? (
                    <>
                        <div className="table-container">
                            <table className="table">
                                <thead>
                                    <tr>
                                        <th>Date</th>
                                        <th>Opération</th>
                                        <th>Type</th>
                                        <th style={{ textAlign: 'right' }}>Montant</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {(transactions?.content || dashboard.recentTransactions).map(tx => (
                                        <tr key={tx.id}>
                                            <td>{formatDate(tx.date)}</td>
                                            <td>{tx.label}</td>
                                            <td>
                                                <span className={`badge badge-${tx.type.toLowerCase()}`}>
                                                    {tx.type === 'CREDIT' ? 'Crédit' : 'Débit'}
                                                </span>
                                            </td>
                                            <td style={{ textAlign: 'right' }}>
                                                <span className={tx.type === 'CREDIT' ? 'amount-credit' : 'amount-debit'}>
                                                    {tx.type === 'CREDIT' ? '+' : '-'}{formatCurrency(tx.amount)}
                                                </span>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>

                        {/* Pagination */}
                        {transactions && transactions.totalPages > 1 && (
                            <div className="pagination">
                                <button
                                    className="btn btn-secondary"
                                    onClick={() => loadTransactions(dashboard.account.id, currentPage - 1)}
                                    disabled={currentPage === 0}
                                >
                                    Précédent
                                </button>
                                <span className="page-info">
                                    Page {currentPage + 1} sur {transactions.totalPages}
                                </span>
                                <button
                                    className="btn btn-secondary"
                                    onClick={() => loadTransactions(dashboard.account.id, currentPage + 1)}
                                    disabled={currentPage >= transactions.totalPages - 1}
                                >
                                    Suivant
                                </button>
                            </div>
                        )}
                    </>
                ) : (
                    <p className="no-transactions">Aucune opération trouvée</p>
                )}
            </div>
        </div>
    );
};

export default Dashboard;
