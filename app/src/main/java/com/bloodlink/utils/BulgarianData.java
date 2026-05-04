package com.bloodlink.utils;

import java.util.*;


public class BulgarianData {

    public static final String[] CITIES = {
        "Благоевград", "Бургас", "Варна", "Велико Търново", "Видин",
        "Враца", "Габрово", "Добрич", "Кърджали", "Кюстендил",
        "Ловеч", "Монтана", "Пазарджик", "Перник", "Плевен",
        "Пловдив", "Разград", "Русе", "Силистра", "Сливен",
        "Смолян", "София", "Стара Загора", "Търговище", "Хасково",
        "Шумен", "Ямбол"
    };


    private static final Map<String, String[]> HOSPITALS_BY_CITY = new LinkedHashMap<>();

    static {
        HOSPITALS_BY_CITY.put("София", new String[]{
            "УМБАЛ Александровска",
            "УМБАЛ Св. Иван Рилски",
            "УМБАЛ Св. Анна",
            "УМБАЛ Царица Йоанна — ИСУЛ",
            "СБАЛАГ Майчин дом",
            "МБАЛ Токуда",
            "МБАЛ Сити Клиник",
            "УМБАЛ Пирогов",
            "ВМА — Военномедицинска академия",
            "МБАЛ НКБ (Национална кардиологична болница)"
        });
        HOSPITALS_BY_CITY.put("Пловдив", new String[]{
            "УМБАЛ Пловдив",
            "МБАЛ Св. Пантелеймон",
            "УМБАЛ Св. Георги",
            "МБАЛ Медика",
            "СБАЛОЗ д-р Марков"
        });
        HOSPITALS_BY_CITY.put("Варна", new String[]{
            "УМБАЛ Св. Марина",
            "МБАЛ Св. Анна — Варна",
            "МБАЛ Варна (ВМА филиал)",
            "СБАЛОЗ — Варна",
            "МБАЛ Провита"
        });
        HOSPITALS_BY_CITY.put("Бургас", new String[]{
            "УМБАЛ Бургас",
            "МБАЛ Бургас АД",
            "ДКЦ Медик — Бургас",
            "СБАЛОЗ — Бургас"
        });
        HOSPITALS_BY_CITY.put("Русе", new String[]{
            "УМБАЛ Медика — Русе",
            "МБАЛ Русе",
            "СБАЛАГ — Русе",
            "ВМА — филиал Русе"
        });
        HOSPITALS_BY_CITY.put("Стара Загора", new String[]{
            "УМБАЛ Проф. д-р Стоян Киркович",
            "МБАЛ Стара Загора",
            "СБАЛОЗ — Стара Загора"
        });
        HOSPITALS_BY_CITY.put("Велико Търново", new String[]{
            "МБАЛ Д-р Стефан Черкезов",
            "СБАЛАГ — Велико Търново",
            "ДКЦ Медик — В. Търново"
        });
        HOSPITALS_BY_CITY.put("Плевен", new String[]{
            "УМБАЛ д-р Георги Странски",
            "МБАЛ Плевен",
            "СБАЛОЗ — Плевен"
        });
        HOSPITALS_BY_CITY.put("Благоевград", new String[]{
            "МБАЛ Д-р Братан Шукеров",
            "МБАЛ Благоевград",
            "ДКЦ Медика — Благоевград"
        });
        HOSPITALS_BY_CITY.put("Видин", new String[]{
            "МБАЛ Видин",
            "ДКЦ — Видин"
        });
        HOSPITALS_BY_CITY.put("Враца", new String[]{
            "МБАЛ Христо Ботев — Враца",
            "СБАЛОЗ — Враца"
        });
        HOSPITALS_BY_CITY.put("Габрово", new String[]{
            "МБАЛ Д-р Тота Венкова",
            "ДКЦ — Габрово"
        });
        HOSPITALS_BY_CITY.put("Добрич", new String[]{
            "МБАЛ Добрич",
            "ДКЦ Добрич"
        });
        HOSPITALS_BY_CITY.put("Кърджали", new String[]{
            "МБАЛ Кърджали",
            "ДКЦ — Кърджали"
        });
        HOSPITALS_BY_CITY.put("Кюстендил", new String[]{
            "МБАЛ Д-р Никола Василиев",
            "ДКЦ Кюстендил"
        });
        HOSPITALS_BY_CITY.put("Ловеч", new String[]{
            "МБАЛ Проф. д-р Параскев Стоянов",
            "ДКЦ — Ловеч"
        });
        HOSPITALS_BY_CITY.put("Монтана", new String[]{
            "МБАЛ Д-р Стамен Илиев",
            "ДКЦ — Монтана"
        });
        HOSPITALS_BY_CITY.put("Пазарджик", new String[]{
            "МБАЛ Пазарджик",
            "ДКЦ — Пазарджик"
        });
        HOSPITALS_BY_CITY.put("Перник", new String[]{
            "МБАЛ Рахила Ангелова",
            "ДКЦ — Перник"
        });
        HOSPITALS_BY_CITY.put("Разград", new String[]{
            "МБАЛ Св. Иван Рилски — Разград",
            "ДКЦ — Разград"
        });
        HOSPITALS_BY_CITY.put("Силистра", new String[]{
            "МБАЛ Силистра",
            "ДКЦ — Силистра"
        });
        HOSPITALS_BY_CITY.put("Сливен", new String[]{
            "МБАЛ Д-р Иван Селимински",
            "ДКЦ — Сливен"
        });
        HOSPITALS_BY_CITY.put("Смолян", new String[]{
            "МБАЛ Смолян",
            "ДКЦ — Смолян"
        });
        HOSPITALS_BY_CITY.put("Търговище", new String[]{
            "МБАЛ Д-р Анастас Пелтеков",
            "ДКЦ — Търговище"
        });
        HOSPITALS_BY_CITY.put("Хасково", new String[]{
            "МБАЛ Хасково",
            "ДКЦ — Хасково"
        });
        HOSPITALS_BY_CITY.put("Шумен", new String[]{
            "МБАЛ Шумен",
            "ДКЦ — Шумен"
        });
        HOSPITALS_BY_CITY.put("Ямбол", new String[]{
            "МБАЛ Св. Пантелеймон — Ямбол",
            "ДКЦ — Ямбол"
        });
    }


    public static String[] getHospitalsForCity(String city) {
        String[] hospitals = HOSPITALS_BY_CITY.get(city);
        if (hospitals != null) return hospitals;
        return new String[]{ "МБАЛ " + city, "ДКЦ " + city };
    }

    public static boolean isKnownCity(String city) {
        return HOSPITALS_BY_CITY.containsKey(city);
    }
}
