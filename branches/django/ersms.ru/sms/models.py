from django.db import models
from django import forms


class Sms(models.Model):
    #полный текст сообщения, отправленного абонентом (включая префикс)
    msg              = models.CharField(max_length=256)
    #транслитерация сообщения
    msg_trans        = models.CharField(max_length=256)
    # дата получения SMS платформой. Формат даты YYYY-MM-DD hh:mm:ss. Знаки препинания передаются в URL кодировке
    date             = models.CharField(max_length=20)
    #короткое название оператора
    operator         = models.CharField(max_length=32)
    #числовой идентификатор оператора
    operator_id      = models.CharField(max_length=8)
    #номер абонента
    user_id          = models.CharField(max_length=32)
    #уникальный числовой идентификатор SMS-сообщения
    smsid            = models.CharField(max_length=32)
    #доход партнера
    cost             = models.CharField(max_length=32)
    #валюта дохода партнера. Передается краткое обозначение валюты, используемое платформой
    currency         = models.CharField(max_length=8)
    #стоимость SMS для абонента (без НДС)
    abonent_cost     = models.CharField(max_length=8)
    #валюта абонента
    abonent_currency = models.CharField(max_length=8)
    #короткий номер
    num              = models.CharField(max_length=8)
    #числовой идентификатор страны
    country_id       = models.CharField(max_length=8)
    #параметр безопасности, значение 1 (подозрение на серию номеров) или 0. 
    # Подозрение на серию номеров возникает, если в течение недели платформа 
    # получает более трех SMS с номеров, в которых одинаковы все цифры, кроме трех последних
    trust            = models.CharField(max_length=1)

    ### Необязательные (опциональные) параметры ###

    #параметр безопасности. Строка (без шифрования) создается партнером системы и позволяет определить, что запрос обрабатывается нашей платформой
    skey             = models.CharField(blank=True, max_length=32)
    #тестовый запрос, значение 0 (рабочий) или 1 (тестовый). Как правило, в рабочем SMS данный параметр отсутствует
    test             = models.CharField(blank=True, max_length=1)
    #счетчик количества обращений к URL партнерского обработчика в пределах одной сессии.
    attempt          = models.CharField(blank=True, max_length=32)
    #параметр повтора, содержит номер повтора в случае, если не удалось установить соединение с сервером партнера.
    retry            = models.CharField(blank=True, max_length=8)
    #параметр, указывающий, какой тип тарификации абонента используется. 
    logic            = models.CharField(blank=True, max_length=1)
    #дата приема СМС
    pub_date         = models.DateTimeField(auto_now=True)
    
