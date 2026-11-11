-- ====================================================================
-- SCRIPT SEED DATA AWAL DUMMY UNTUK SUPABASE DATABASE MANAGEVENT
-- Jalankan script SQL ini di Dashboard Supabase > SQL Editor
-- ====================================================================

-- 1. SEED DATA TABEL EVENTS
INSERT INTO public.events (id, title, description, category, date, location, price, image_url)
VALUES
    (
        '11111111-1111-1111-1111-111111111111',
        'Pameran Pernikahan Nusantara 2026',
        'Pameran vendor pernikahan terbesar di Nusantara dengan promo eksklusif gaun, catering, dekorasi, dan dokumentasi terlengkap.',
        'Pernikahan',
        '18 Juli 2026',
        'Jakarta Convention Center',
        150000.00,
        'https://images.unsplash.com/photo-1519741497674-611481863552?q=80&w=800'
    ),
    (
        '22222222-2222-2222-2222-222222222222',
        'Jakarta Jazz Symphony 2026',
        'Konser kolaborasi musisi jazz ternama tanah air dan orkestra internasional dengan penampilan panggung megah.',
        'Konser',
        '22 Agustus 2026',
        'Istora Senayan, Jakarta',
        350000.00,
        'https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?q=80&w=800'
    ),
    (
        '33333333-3333-3333-3333-333333333333',
        'Indonesia Tech Summit 2027',
        'Konferensi teknologi & AI terbesar tahun 2027 menghadirkan pakar industri global, pameran startup, dan sesi networking.',
        'Seminar',
        '14 Februari 2027',
        'ICE BSD Tangerang',
        250000.00,
        'https://images.unsplash.com/photo-1540575467063-178a50c2df87?q=80&w=800'
    ),
    (
        '44444444-4444-4444-4444-444444444444',
        'Kids & Family Birthday Festival',
        'Festival ulang tahun anak dan keluarga terbesar dengan wahana permainan, panggung pertunjukan boneka, serta workshop kreasi.',
        'Ulang Tahun',
        '10 Mei 2026',
        'Grand City Surabaya',
        75000.00,
        'https://images.unsplash.com/photo-1530103862676-de8c9debad1d?q=80&w=800'
    ),
    (
        '55555555-5555-5555-5555-555555555555',
        'Nusantara Symphony Fest',
        'Konser musik simfoni megah menyajikan karya maestro Nusantara dengan orkestra lengkap.',
        'Konser',
        '14 Mei 2026',
        'Gelora Bung Karno, Jakarta',
        250000.00,
        'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?q=80&w=800'
    ),
    (
        '66666666-6666-6666-6666-666666666666',
        'Gelaran Nikah Adat Nusantara',
        'Pameran gaun dan adat pernikahan tradisional terlengkap dari Sabang sampai Merauke.',
        'Pernikahan',
        '22 Agustus 2026',
        'JCC Senayan, Jakarta',
        75000.00,
        'https://images.unsplash.com/photo-1519741497674-611481863552?q=80&w=800'
    ),
    (
        '77777777-7777-7777-7777-777777777777',
        'Bandung Rasa & Kopi Fest',
        'Festival kuliner dan kopi terbesar se-Jawa Barat menghadirkan lebih dari 100 tenant kuliner terfavorit.',
        'Festival',
        '10 Oktober 2026',
        'Jalan Braga, Bandung',
        50000.00,
        'https://images.unsplash.com/photo-1501386761578-eac5c94b800a?q=80&w=800'
    ),
    (
        '88888888-8888-8888-8888-888888888888',
        'Bali Immersive Art Expo',
        'Pameran seni digital imersif di Ubud Bali menampilkan kolaborasi seniman lokal dan internasional.',
        'Pameran',
        '15 Januari 2027',
        'Ubud Art Center, Bali',
        180000.00,
        'https://images.unsplash.com/photo-1508997449629-303059a039c0?q=80&w=800'
    )
ON CONFLICT (id) DO UPDATE SET
    title = EXCLUDED.title,
    description = EXCLUDED.description,
    category = EXCLUDED.category,
    date = EXCLUDED.date,
    location = EXCLUDED.location,
    price = EXCLUDED.price,
    image_url = EXCLUDED.image_url;


-- 2. SEED DATA TABEL ULASAN
INSERT INTO public.ulasan (id, event_id, username, rating, comment)
VALUES
    (
        gen_random_uuid(),
        '11111111-1111-1111-1111-111111111111',
        'Sarah Wijaya',
        5,
        'Pamerannya sangat lengkap! Vendor dekorasi dan cateringnya ramah-ramah serta banyak potongan diskon.'
    ),
    (
        gen_random_uuid(),
        '11111111-1111-1111-1111-111111111111',
        'Budi Pratama',
        4,
        'Acara tertata rapi, tiket digital di aplikasi langsung discan tanpa antre panjang.'
    ),
    (
        gen_random_uuid(),
        '22222222-2222-2222-2222-222222222222',
        'Dina Sastro',
        5,
        'Sound system dan pencahayaan panggung jazz-nya spektakuler sekali!'
    )
ON CONFLICT (id) DO NOTHING;
