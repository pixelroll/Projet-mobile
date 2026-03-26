export interface TripPoint {
  id: string;
  name: string;
  lat: number;
  lng: number;
  image: string;
  postId?: string;
}

export interface Trip {
  id: string;
  title: string;
  description?: string;
  destination: string;
  duration: string;
  budget: string;
  places: number;
  coverImage: string;
  center: [number, number];
  zoom: number;
  path: [number, number][];
  points: TripPoint[];
}

export const mockTrips: Trip[] = [
  {
    id: "paris",
    title: "Paris Romantique",
    description: "Une escapade inoubliable de 3 jours au cœur de la capitale française, entre monuments historiques et gastronomie exquise.",
    destination: "Paris, France",
    duration: "3 jours",
    budget: "450 €",
    places: 4,
    coverImage:
      "https://images.unsplash.com/photo-1431274172761-fca41d930114?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxwYXJpcyUyMGVpZmZlbCUyMHRvd2VyfGVufDF8fHx8MTc3MzczNTUwOXww&ixlib=rb-4.1.0&q=80&w=1080",
    center: [48.86, 2.33],
    zoom: 13,
    path: [
      [48.8584, 2.2945], // Eiffel Tower
      [48.8738, 2.295],  // Arc de Triomphe
      [48.8606, 2.3376], // Louvre
      [48.8529, 2.3499], // Notre Dame
    ],
    points: [
      {
        id: "p1",
        name: "Tour Eiffel",
        lat: 48.8584,
        lng: 2.2945,
        image: "https://images.unsplash.com/photo-1543349689-9a4d426bee8e?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxlaWZmZWwlMjB0b3dlcnxlbnwxfHx8fDE3MTI1MjMwMzh8MA&ixlib=rb-4.1.0&q=80&w=1080",
        postId: "1",
      },
      {
        id: "p2",
        name: "Arc de Triomphe",
        lat: 48.8738,
        lng: 2.295,
        image: "https://images.unsplash.com/photo-1509303531649-14a872658826?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxhcmMlMjBkZSUyMHRyaW9tcGhlfGVufDF8fHx8MTcxMjUyMzA5MHww&ixlib=rb-4.1.0&q=80&w=1080",
      },
      {
        id: "p3",
        name: "Musée du Louvre",
        lat: 48.8606,
        lng: 2.3376,
        image: "https://images.unsplash.com/photo-1499856871958-5b9627545d1a?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxsb3V2cmV8ZW58MXx8fHwxNzEyNTIzMTQwfDA&ixlib=rb-4.1.0&q=80&w=1080",
      },
      {
        id: "p4",
        name: "Notre-Dame",
        lat: 48.8529,
        lng: 2.3499,
        image: "https://images.unsplash.com/photo-1552839446-5f504de71d3c?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxub3RyZSUyMGRhbWV8ZW58MXx8fHwxNzEyNTIzMTcwfDA&ixlib=rb-4.1.0&q=80&w=1080",
      },
    ],
  },
  {
    id: "tokyo",
    title: "Aventure à Tokyo",
    description: "Une semaine d'exploration de la métropole vibrante, alliant tradition ancienne et technologie futuriste.",
    destination: "Tokyo, Japon",
    duration: "7 jours",
    budget: "1 200 €",
    places: 4,
    coverImage:
      "https://images.unsplash.com/photo-1626946548234-a65fd193db41?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHx0b2t5byUyMHN0cmVldCUyMG5pZ2h0fGVufDF8fHx8MTc3Mzc1Njk2N3ww&ixlib=rb-4.1.0&q=80&w=1080",
    center: [35.69, 139.75],
    zoom: 12,
    path: [
      [35.6595, 139.7005], // Shibuya
      [35.6938, 139.7034], // Shinjuku
      [35.6983, 139.7731], // Akihabara
      [35.7147, 139.7967], // Asakusa
    ],
    points: [
      {
        id: "t1",
        name: "Shibuya Crossing",
        lat: 35.6595,
        lng: 139.7005,
        image: "https://images.unsplash.com/photo-1542051812-bf2a9a834479?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxzaGlidXlhfGVufDF8fHx8MTcxMjUyMzIwMHww&ixlib=rb-4.1.0&q=80&w=1080",
        postId: "2",
      },
      {
        id: "t2",
        name: "Shinjuku",
        lat: 35.6938,
        lng: 139.7034,
        image: "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxzaGluanVrdXxlbnwxfHx8fDE3MTI1MjMyNDV8MA&ixlib=rb-4.1.0&q=80&w=1080",
      },
      {
        id: "t3",
        name: "Akihabara",
        lat: 35.6983,
        lng: 139.7731,
        image: "https://images.unsplash.com/photo-1533050487297-09b450131914?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxha2loYWJhcmF8ZW58MXx8fHwxNzEyNTIzMjkzfDA&ixlib=rb-4.1.0&q=80&w=1080",
      },
      {
        id: "t4",
        name: "Senso-ji (Asakusa)",
        lat: 35.7147,
        lng: 139.7967,
        image: "https://images.unsplash.com/photo-1590559899731-a382839e5549?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxzZW5zb2ppfGVufDF8fHx8MTcxMjUyMzM0Nnww&ixlib=rb-4.1.0&q=80&w=1080",
      },
    ],
  },
  {
    id: "barcelona",
    title: "Barcelone Culturelle",
    description: "4 jours sous le soleil catalan, avec l'architecture d'Antoni Gaudí et la vie animée des Ramblas.",
    destination: "Barcelone, Espagne",
    duration: "4 jours",
    budget: "600 €",
    places: 4,
    coverImage:
      "https://images.unsplash.com/photo-1653677903266-1d814985b3cc?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxiYXJjZWxvbmElMjBhcmNoaXRlY3R1cmV8ZW58MXx8fHwxNzczNzM4MzE3fDA&ixlib=rb-4.1.0&q=80&w=1080",
    center: [41.39, 2.16],
    zoom: 13,
    path: [
      [41.3818, 2.1721], // La Boqueria
      [41.3916, 2.1649], // Casa Batllo
      [41.4036, 2.1744], // Sagrada Familia
      [41.4145, 2.1527], // Park Guell
    ],
    points: [
      {
        id: "b1",
        name: "La Boqueria",
        lat: 41.3818,
        lng: 2.1721,
        image: "https://images.unsplash.com/photo-1589979313437-1422bb9dd888?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxsYSUyMGJvcXVlcmlhfGVufDF8fHx8MTcxMjUyMzQwOHww&ixlib=rb-4.1.0&q=80&w=1080",
      },
      {
        id: "b2",
        name: "Casa Batlló",
        lat: 41.3916,
        lng: 2.1649,
        image: "https://images.unsplash.com/photo-1605349477024-fb5fcc4c58fc?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxjYXNhJTIwYmF0bGxvfGVufDF8fHx8MTcxMjUyMzQ0NHww&ixlib=rb-4.1.0&q=80&w=1080",
      },
      {
        id: "b3",
        name: "Sagrada Família",
        lat: 41.4036,
        lng: 2.1744,
        image: "https://images.unsplash.com/photo-1583422409516-2895a77efded?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxzYWdyYWRhJTIwZmFtaWxpYXxlbnwxfHx8fDE3MTI1MjM0OTB8MA&ixlib=rb-4.1.0&q=80&w=1080",
        postId: "3",
      },
      {
        id: "b4",
        name: "Park Güell",
        lat: 41.4145,
        lng: 2.1527,
        image: "https://images.unsplash.com/photo-1558642084-fd07fae5282e?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxwYXJrJTIwZ3VlbGx8ZW58MXx8fHwxNzEyNTIzNTQ1fDA&ixlib=rb-4.1.0&q=80&w=1080",
      },
    ],
  },
];
