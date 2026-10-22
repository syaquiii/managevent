-- ====================================================================
-- SKEMA DATABASE SUPABASE UNTUK APLIKASI MANAGEVENT
-- Salin dan jalankan seluruh script ini di SQL Editor Dashboard Supabase
-- ====================================================================

-- 1. TABEL PROFILES (Detail User Terhubung dengan Supabase Auth)
CREATE TABLE IF NOT EXISTS public.profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    email TEXT NOT NULL,
    full_name TEXT,
    avatar_url TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Public profiles are viewable by everyone"
    ON public.profiles FOR SELECT USING (true);

CREATE POLICY "Users can update their own profile"
    ON public.profiles FOR UPDATE USING (auth.uid() = id);

CREATE POLICY "Users can insert their own profile"
    ON public.profiles FOR INSERT WITH CHECK (auth.uid() = id);


-- Trigger Otomatis Membuat Row Profile Saat User Mendaftar (SignUp)
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO public.profiles (id, email, full_name, avatar_url)
    VALUES (
        NEW.id,
        NEW.email,
        COALESCE(NEW.raw_user_meta_data->>'full_name', ''),
        COALESCE(NEW.raw_user_meta_data->>'avatar_url', '')
    );
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

CREATE POLICY "Public events are viewable by everyone"
    ON public.events FOR SELECT USING (true);

CREATE POLICY "Events are insertable by authenticated users"
    ON public.events FOR INSERT WITH CHECK (true);

CREATE POLICY "Events are updatable by authenticated users"
    ON public.events FOR UPDATE USING (true);

CREATE POLICY "Events are deletable by authenticated users"
    ON public.events FOR DELETE USING (true);


-- 3. TABEL TRANSAKSI (Fitur Pemesanan - Muhammad Darren Achilles Setiawan)
CREATE TABLE IF NOT EXISTS public.transaksi (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
    event_id UUID REFERENCES public.events(id) ON DELETE CASCADE,
    jumlah_tiket INT DEFAULT 1,
    total_harga NUMERIC(12, 2) DEFAULT 0.00,
    status TEXT DEFAULT 'PENDING',
    created_at TIMESTAMPTZ DEFAULT NOW()
);

ALTER TABLE public.transaksi ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can view transactions"
    ON public.transaksi FOR SELECT USING (true);

CREATE POLICY "Users can insert transactions"
    ON public.transaksi FOR INSERT WITH CHECK (true);

CREATE POLICY "Users can update transactions"
    ON public.transaksi FOR UPDATE USING (true);


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

CREATE POLICY "Reviews viewable by everyone"
    ON public.ulasan FOR SELECT USING (true);

CREATE POLICY "Users can insert reviews"
    ON public.ulasan FOR INSERT WITH CHECK (true);


-- 5. TABEL WISHLIST (Fitur Wishlist - Muhammad Syauqi Imaduddin)
CREATE TABLE IF NOT EXISTS public.wishlist (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
    event_id UUID REFERENCES public.events(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    CONSTRAINT unique_user_event_wishlist UNIQUE (user_id, event_id)
);

ALTER TABLE public.wishlist ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Wishlist viewable by users"
    ON public.wishlist FOR SELECT USING (true);

CREATE POLICY "Users can manage wishlist"
    ON public.wishlist FOR ALL USING (true);


-- 6. TABEL AGENDA (Fitur Agenda - Farrel Radya Putra)
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

CREATE POLICY "Agenda viewable by users"
    ON public.agenda FOR SELECT USING (true);

CREATE POLICY "Users can manage agenda"
    ON public.agenda FOR ALL USING (true);
