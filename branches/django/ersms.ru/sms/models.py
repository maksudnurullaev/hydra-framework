from django.db import models
from django import forms


class Sms(models.Model):
    msg              = models.CharField(max_length=256)
    msg_trans        = models.CharField(max_length=256)
    date             = models.CharField(max_length=20)
    operator         = models.CharField(max_length=32)
    operator_id      = models.CharField(max_length=8)
    user_id          = models.CharField(max_length=32)
    smsid            = models.CharField(max_length=32)
    cost             = models.CharField(max_length=32)
    currency         = models.CharField(max_length=8)
    abonent_cost     = models.CharField(max_length=8)
    abonent_currency = models.CharField(max_length=8)
    num              = models.CharField(max_length=8)
    country_id       = models.CharField(max_length=8)
    trust            = models.CharField(max_length=1)

    skey             = models.CharField(blank=True, max_length=32)
    test             = models.CharField(blank=True, max_length=1)
    attempt          = models.CharField(blank=True, max_length=32)
    retry            = models.CharField(blank=True, max_length=8)
    logic            = models.CharField(blank=True, max_length=1)
    pub_date         = models.DateTimeField(auto_now=True)
    
