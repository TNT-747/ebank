import React, { useState } from 'react';
import { clientAPI } from '../services/api';
import './Forms.css';

const NewClient = () => {
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        identityNumber: '',
        birthDate: '',
        email: '',
        address: '',
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

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
            const response = await clientAPI.create(formData);
            setSuccess(response.data.message);
            setFormData({
                firstName: '',
                lastName: '',
                identityNumber: '',
                birthDate: '',
                email: '',
                address: '',
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
                    <h1 className="form-title">Ajouter un Nouveau Client</h1>
                    <p className="form-subtitle">Remplissez les informations du client</p>
                </div>

                {error && <div className="alert alert-error">{error}</div>}
                {success && <div className="alert alert-success">{success}</div>}

                <form onSubmit={handleSubmit}>
                    <div className="form-row">
                        <div className="form-group">
                            <label className="form-label">Prénom *</label>
                            <input
                                type="text"
                                name="firstName"
                                className="form-input"
                                placeholder="Ahmed"
                                value={formData.firstName}
                                onChange={handleChange}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label className="form-label">Nom *</label>
                            <input
                                type="text"
                                name="lastName"
                                className="form-input"
                                placeholder="Kassimi"
                                value={formData.lastName}
                                onChange={handleChange}
                                required
                            />
                        </div>
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label className="form-label">Numéro d'identité *</label>
                            <input
                                type="text"
                                name="identityNumber"
                                className="form-input"
                                placeholder="AB123456"
                                value={formData.identityNumber}
                                onChange={handleChange}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label className="form-label">Date de naissance *</label>
                            <input
                                type="date"
                                name="birthDate"
                                className="form-input"
                                value={formData.birthDate}
                                onChange={handleChange}
                                required
                            />
                        </div>
                    </div>

                    <div className="form-group">
                        <label className="form-label">Adresse email *</label>
                        <input
                            type="email"
                            name="email"
                            className="form-input"
                            placeholder="client@example.com"
                            value={formData.email}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label className="form-label">Adresse postale *</label>
                        <input
                            type="text"
                            name="address"
                            className="form-input"
                            placeholder="123 Avenue Mohammed V, Casablanca"
                            value={formData.address}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <button
                        type="submit"
                        className="btn btn-primary submit-btn"
                        disabled={loading}
                    >
                        {loading ? 'Création en cours...' : 'Créer le Client'}
                    </button>
                </form>

                <div className="form-info">
                    <p>
                        <strong>Note:</strong> Un email sera envoyé au client avec ses identifiants de connexion.
                    </p>
                </div>
            </div>
        </div>
    );
};

export default NewClient;
