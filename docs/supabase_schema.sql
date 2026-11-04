-- ====================================================================
-- SKEMA DATABASE SUPABASE UNTUK APLIKASI MANAGEVENT (IDEMPOTENT / RE-RUNNABLE)
-- Salin dan jalankan seluruh script ini di SQL Editor Dashboard Supabase
-- ====================================================================

-- 1. TABEL PROFILES (Detail User Terhubung dengan Supabase Auth + RBAC Role)
CREATE TABLE IF NOT EXISTS public.profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    email TEXT NOT NULL,
    full_name TEXT,
    avatar_url TEXT,
    role TEXT DEFAULT 'PEMESAN', -- 'VENDOR' atau 'PEMESAN'
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Pastikan kolom role ada jika tabel sudah dibuat sebelumnya
ALTER TABLE public.profiles ADD COLUMN IF NOT EXISTS role TEXT DEFAULT 'PEMESAN';

ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Public profiles are viewable by everyone" ON public.profiles;
CREATE POLICY "Public profiles are viewable by everyone"
    ON public.profiles FOR SELECT USING (true);

DROP POLICY IF EXISTS "Users can update their own profile" ON public.profiles;
CREATE POLICY "Users can update their own profile"
    ON public.profiles FOR UPDATE USING (auth.uid() = id);

DROP POLICY IF EXISTS "Users can insert their own profile" ON public.profiles;
CREATE POLICY "Users can insert their own profile"
    ON public.profiles FOR INSERT WITH CHECK (auth.uid() = id);


-- Trigger Otomatis Membuat Row Profile Saat User Mendaftar (SignUp)
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO public.profiles (id, email, full_name, avatar_url, role)
    VALUES (
        NEW.id,
        NEW.email,
        COALESCE(NEW.raw_user_meta_data->>'full_name', ''),
        COALESCE(NEW.raw_user_meta_data->>'avatar_url', ''),
        COALESCE(NEW.raw_user_meta_data->>'role', 'PEMESAN')
    )
    ON CONFLICT (id) DO UPDATE SET
        email = EXCLUDED.email,
        full_name = COALESCE(EXCLUDED.full_name, public.profiles.full_name),
        avatar_url = COALESCE(EXCLUDED.avatar_url, public.profiles.avatar_url),
        role = COALESCE(EXCLUDED.role, public.profiles.role);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;
CREATE TRIGGER on_auth_user_created
    AFTER INSERT ON auth.users
    FOR EACH ROW EXECUTE FUNCTION public.handle_new_user();


-- 2. TABEL EVENTS (Fitur Katalog - Muhammad Abdul Fatah)
CREATE TABLE IF NOT EXISTS public.events (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    title TEXT NOT NULL,
    description TEXT,
    category TEXT,
    date TEXT,
    location TEXT,
    price NUMERIC(12, 2) DEFAULT 0.00,
    image_url TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

ALTER TABLE public.events ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Public events are viewable by everyone" ON public.events;
CREATE POLICY "Public events are viewable by everyone"
    ON public.events FOR SELECT USING (true);

DROP POLICY IF EXISTS "Events are insertable by authenticated users" ON public.events;
CREATE POLICY "Events are insertable by authenticated users"
    ON public.events FOR INSERT WITH CHECK (true);

DROP POLICY IF EXISTS "Events are updatable by authenticated users" ON public.events;
CREATE POLICY "Events are updatable by authenticated users"
    ON public.events FOR UPDATE USING (true);

DROP POLICY IF EXISTS "Events are deletable by authenticated users" ON public.events;
CREATE POLICY "Events are deletable by authenticated users"
    ON public.events FOR DELETE USING (true);


-- 3. TABEL TRANSAKSI (Fitur Pemesanan - Muhammad Darren Achilles Setiawan)
CREATE TABLE IF NOT EXISTS public.transaksi (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
    event_id UUID REFERENCES public.events(id) ON DELETE CASCADE,
    jumlah_tiket INT DEFAULT 1,
    catatan TEXT,
    total_harga NUMERIC(12, 2) DEFAULT 0.00,
    status TEXT DEFAULT 'Berjalan', -- 'Berjalan' atau 'Selesai'
    created_at TIMESTAMPTZ DEFAULT NOW()
);

ALTER TABLE public.transaksi ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Users can view transactions" ON public.transaksi;
CREATE POLICY "Users can view transactions"
    ON public.transaksi FOR SELECT USING (true);

DROP POLICY IF EXISTS "Users can insert transactions" ON public.transaksi;
CREATE POLICY "Users can insert transactions"
    ON public.transaksi FOR INSERT WITH CHECK (true);

DROP POLICY IF EXISTS "Users can update transactions" ON public.transaksi;
CREATE POLICY "Users can update transactions"
    ON public.transaksi FOR UPDATE USING (true);

DROP POLICY IF EXISTS "Users can delete transactions" ON public.transaksi;
CREATE POLICY "Users can delete transactions"
    ON public.transaksi FOR DELETE USING (true);


-- 4. TABEL ULASAN (Fitur Ulasan - Rayhan Wira Pamungkas)
CREATE TABLE IF NOT EXISTS public.ulasan (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    event_id UUID REFERENCES public.events(id) ON DELETE CASCADE,
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
    username TEXT,
    rating INT CHECK (rating >= 1 AND rating <= 5) DEFAULT 5,
    comment TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

ALTER TABLE public.ulasan ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Reviews viewable by everyone" ON public.ulasan;
CREATE POLICY "Reviews viewable by everyone"
    ON public.ulasan FOR SELECT USING (true);

DROP POLICY IF EXISTS "Users can insert reviews" ON public.ulasan;
CREATE POLICY "Users can insert reviews"
    ON public.ulasan FOR INSERT WITH CHECK (true);

DROP POLICY IF EXISTS "Users can update reviews" ON public.ulasan;
CREATE POLICY "Users can update reviews"
    ON public.ulasan FOR UPDATE USING (true);

DROP POLICY IF EXISTS "Users can delete reviews" ON public.ulasan;
CREATE POLICY "Users can delete reviews"
    ON public.ulasan FOR DELETE USING (true);


-- 5. TABEL WISHLIST (Fitur Wishlist - Muhammad Syauqi Imaduddin)
CREATE TABLE IF NOT EXISTS public.wishlist (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
    event_id UUID REFERENCES public.events(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    CONSTRAINT unique_user_event_wishlist UNIQUE (user_id, event_id)
);

ALTER TABLE public.wishlist ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Wishlist viewable by users" ON public.wishlist;
CREATE POLICY "Wishlist viewable by users"
    ON public.wishlist FOR SELECT USING (true);

DROP POLICY IF EXISTS "Users can manage wishlist" ON public.wishlist;
CREATE POLICY "Users can manage wishlist"
    ON public.wishlist FOR ALL USING (true);


-- 6. TABEL AGENDA / CATATAN PERSIAPAN (Fitur Catatan - Farrel Radya Putra)
CREATE TABLE IF NOT EXISTS public.agenda (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
    event_id UUID REFERENCES public.events(id) ON DELETE SET NULL,
    title TEXT NOT NULL,
    date TEXT NOT NULL,
    time TEXT,
    notes TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

ALTER TABLE public.agenda ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Agenda viewable by users" ON public.agenda;
CREATE POLICY "Agenda viewable by users"
    ON public.agenda FOR SELECT USING (true);

DROP POLICY IF EXISTS "Users can manage agenda" ON public.agenda;
CREATE POLICY "Users can manage agenda"
    ON public.agenda FOR ALL USING (true);
