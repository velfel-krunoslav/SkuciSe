const express = require('express');
const Realties = require('../models/Realties');
const router = express.Router();

router.get('/', (req, res) => {
    Realties.readAll()
    .then( realties => {
        res.status(200).json(realties)
    })
    .catch( err => {
        res.status(500).json(err)
    })
});

router.get('/:id', (req, res) => {
    const { id } = req.params;
    Realties.read(id)
    .then( realty => {
        res.status(200).json(realty);
    })
    .catch( err => {
        res.status(500).json(err);
    })
});

router.post('/', (req, res) => {
    const data = req.body;

    Realties.create(data)
    .then( realty => {
        res.status(200).json(realty);
    })
    .catch( err => {
        console.log(err);
        res.status(500).json(err);
    })
});

router.delete('/:id', (req, res) => {
    const { id } = req.params;

    Realties.del(id)
    .then( num => {
        res.status(200).json(num);
    })
    .catch( err => {
        console.log(err);
        res.status(500).json(err);
    })
});


module.exports = router;