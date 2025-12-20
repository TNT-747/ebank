import React, { useState } from 'react';
import { accountAPI } from '../services/api';
import './Forms.css';

const NewAccount = () => {
    const [formData, setFormData] = useState({
        rib: '',
        identityNumber: '',
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value.toUpperCase() }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        setLoading(true);

        try {
            const response = await accountAPI.create(formData);
            setSuccess(response.data.message);
            setFormData({
                rib: '',
                identityNumber: '',
            });
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="form-page">
            <div className="form-container animate-slideUp">
                <div className="form-header">
                    <h1 className="form-title">Nouveau Compte Bancaire</h1>
                    <p className="form-subtitle">Créez un nouveau compte pour un client existant</p>
                </div>

                {error && <div className="alert alert-error">{error}</div>}
                {success && <div className="alert alert-success">{success}</div>}

                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label className="form-label">Numéro d'identité du client *</label>
                        <input
                            type="text"
                            name="identityNumber"
                            className="form-input"
                            placeholder="AB123456"
                            value={formData.identityNumber}
                            onChange={handleChange}
                            required
                        />
                        <span className="form-hint">Le client doit exister dans la base de données</span>
                    </div>

                    <div className="form-group">
                        <label className="form-label">RIB (Relevé d'Identité Bancaire) *</label>
                        <input
                            type="text"
                            name="rib"
                            className="form-input"
                            placeholder="MA64001128000012345678901234"
                            value={formData.rib}
                            onChange={handleChange}
                            required
                        />
                        <span className="form-hint">Format IBAN valide requis (ex: MA64...)</span>
                    </div>

                    <button
                        type="submit"
                        className="btn btn-primary submit-btn"
                        disabled={loading}
                    >
                        {loading ? 'Création en cours...' : 'Créer le Compte'}
                    </button>
                </form>

                <div className="form-info">
                    <p>
                        <strong>Note:</strong> Le compte sera créé avec un statut "Ouvert" et un solde initial de 0.
                    </p>
                </div>
            </div>
        </div>
    );
};

export default NewAccount;
