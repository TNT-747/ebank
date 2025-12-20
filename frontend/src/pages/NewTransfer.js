import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { dashboardAPI, transferAPI } from '../services/api';
import './Forms.css';

const NewTransfer = () => {
    const [accounts, setAccounts] = useState([]);
    const [formData, setFormData] = useState({
        sourceRib: '',
        destinationRib: '',
        amount: '',
        motif: '',
    });
    const [loading, setLoading] = useState(false);
    const [loadingAccounts, setLoadingAccounts] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const navigate = useNavigate();

    useEffect(() => {
        loadAccounts();
    }, []);

    const loadAccounts = async () => {
        try {
            const response = await dashboardAPI.get();
            const dashboardData = response.data.data;
            setAccounts(dashboardData.allAccounts || []);

            if (dashboardData.allAccounts?.length > 0) {
                setFormData(prev => ({
                    ...prev,
                    sourceRib: dashboardData.allAccounts[0].rib,
                }));
            }
        } catch (err) {
            setError(err.message);
        } finally {
            setLoadingAccounts(false);
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        setLoading(true);

        try {
            const response = await transferAPI.execute({
                ...formData,
                amount: parseFloat(formData.amount),
            });
            setSuccess(response.data.message);

            // Redirect to dashboard after 2 seconds
            setTimeout(() => {
                navigate('/dashboard');
            }, 2000);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('fr-MA', {
            style: 'currency',
            currency: 'MAD',
        }).format(amount);
    };

    const selectedAccount = accounts.find(a => a.rib === formData.sourceRib);

    if (loadingAccounts) {
        return (
            <div className="form-page">
                <div className="form-container">
                    <div className="loading-state">
                        <div className="loader"></div>
                        <p>Chargement de vos comptes...</p>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="form-page">
            <div className="form-container animate-slideUp">
                <div className="form-header">
                    <h1 className="form-title">Nouveau Virement</h1>
                    <p className="form-subtitle">Effectuez un virement vers un autre compte</p>
                </div>

                {error && <div className="alert alert-error">{error}</div>}
                {success && <div className="alert alert-success">{success}</div>}

                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label className="form-label">Compte source *</label>
                        {accounts.length > 1 ? (
                            <select
                                name="sourceRib"
                                className="form-select"
                                value={formData.sourceRib}
                                onChange={handleChange}
                                required
                            >
                                {accounts.map(account => (
                                    <option key={account.id} value={account.rib}>
                                        {account.rib} - Solde: {formatCurrency(account.balance)}
                                    </option>
                                ))}
                            </select>
                        ) : (
                            <input
                                type="text"
                                className="form-input"
                                value={formData.sourceRib}
                                disabled
                            />
                        )}
                        {selectedAccount && (
                            <span className="form-hint">
                                Solde disponible: <strong>{formatCurrency(selectedAccount.balance)}</strong>
                            </span>
                        )}
                    </div>

                    <div className="form-group">
                        <label className="form-label">RIB destinataire *</label>
                        <input
                            type="text"
                            name="destinationRib"
                            className="form-input"
                            placeholder="MA64001128000098765432109876"
                            value={formData.destinationRib}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label className="form-label">Montant (MAD) *</label>
                        <input
                            type="number"
                            name="amount"
                            className="form-input"
                            placeholder="1000.00"
                            min="0.01"
                            step="0.01"
                            value={formData.amount}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label className="form-label">Motif du virement *</label>
                        <input
                            type="text"
                            name="motif"
                            className="form-input"
                            placeholder="Paiement facture, remboursement..."
                            value={formData.motif}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <button
                        type="submit"
                        className="btn btn-primary submit-btn"
                        disabled={loading || !formData.sourceRib}
                    >
                        {loading ? 'Traitement en cours...' : 'Valider le Virement'}
                    </button>
                </form>

                <div className="form-info form-info-warning">
                    <p>
                        <strong>Attention:</strong> Vérifiez bien le RIB destinataire avant de confirmer.
                        Cette opération est irréversible.
                    </p>
                </div>
            </div>
        </div>
    );
};

export default NewTransfer;
